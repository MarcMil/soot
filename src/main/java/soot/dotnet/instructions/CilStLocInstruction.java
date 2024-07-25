package soot.dotnet.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2022 Fraunhofer SIT
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import soot.Body;
import soot.FastHierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.dotnet.exceptions.NoExpressionInstructionException;
import soot.dotnet.members.method.DotnetBody;
import soot.dotnet.proto.ProtoAssemblyAllTypes.TypeKindDef;
import soot.dotnet.proto.ProtoIlInstructions;
import soot.dotnet.types.DotnetBasicTypes;
import soot.dotnet.types.DotnetType;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

/**
 * AssignStmt - Store a expression to a local Make additional tasks for rewriting .NET opcodes to Jimple
 */
public class CilStLocInstruction extends AbstractCilnstruction {
  public CilStLocInstruction(ProtoIlInstructions.IlInstructionMsg instruction, DotnetBody dotnetBody, CilBlock cilBlock) {
    super(instruction, dotnetBody, cilBlock);
  }

  @Override
  public void jimplify(Body jb) {
    CilInstruction cilExpr
        = CilInstructionFactory.fromInstructionMsg(instruction.getValueInstruction(), dotnetBody, cilBlock);
    Local variable = dotnetBody.variableManager.addOrGetVariable(instruction.getVariable(), jb);
    Value value = cilExpr.jimplifyExpr(jb);

    // if rvalue is isinst, change to instanceof semantic, see description of opcode
    if (cilExpr instanceof CilIsInstInstruction) {
      CilIsInstInstruction isInst = (CilIsInstInstruction) cilExpr;
      isInst.resolveRewritingIsInst(jb, variable, value);
      return;
    }

    // rewrite multi array to Jimple semantic, see description of opcode
    if (cilExpr instanceof CilLdElemaInstruction && ((CilLdElemaInstruction) cilExpr).isMultiArrayRef()) {
      CilLdElemaInstruction newArrInstruction = (CilLdElemaInstruction) cilExpr;
      value = newArrInstruction.resolveRewriteMultiArrAccess(jb);
    }

    // create this cast, to validate successfully
    if (value instanceof Local && !variable.getType().toString().equals(value.getType().toString())
        && dotnetBody.variableManager.localsToCastContains(((Local) value).getName())) {
      value = Jimple.v().newCastExpr(value, variable.getType());
    }
    // for validation, because array = obj, where array typeof byte[] and obj typeof System.Object
    if (value instanceof Local && value.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)
        && !variable.getType().toString().equals(DotnetBasicTypes.SYSTEM_OBJECT)) {
      value = Jimple.v().newCastExpr(value, variable.getType());
    }

    final FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
    final RefType valueType = RefType.v("System.ValueType");

    Type modType = variable.getType();
    TypeKindDef varkind = instruction.getVariable().getType().getTypeKind();
    if (varkind != TypeKindDef.BY_REF && fh.canStoreType(modType, valueType) && !(value instanceof Constant)) {
      // we need to copy structs!
      RefType rt = (RefType) modType;
      SootMethod cm = DotnetType.getCopyMethod(rt.getSootClass());
      if (cm != null) {
        Jimple j = Jimple.v();
        boolean doCopy = true;
        if (!(value instanceof Local)) {
          if (value instanceof NewExpr) {
            doCopy = false;
          }
          value = simplifyComplexExpression(jb, value);
        }
        if (doCopy) {
          value = createTempVar(jb, j, j.newSpecialInvokeExpr((Local) value, cm.makeRef()));
        }
      }
    }
    AssignStmt astm = Jimple.v().newAssignStmt(variable, value);
    jb.getUnits().add(astm);
  }

  @Override
  public Value jimplifyExpr(Body jb) {
    throw new NoExpressionInstructionException(instruction);
  }
}
