/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jimple.toolkits.annotation.callgraph;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.tagkit.*;
import soot.jimple.*;
import soot.util.queue.*;
import soot.jimple.toolkits.callgraph.*;
import soot.toolkits.graph.interaction.*;
import soot.options.*;

/** A scene transformer that creates a graphical callgraph. */
public class CallGraphGrapher extends SceneTransformer
{ 
    public CallGraphGrapher(Singletons.Global g){}
    public static CallGraphGrapher v() { return G.v().soot_jimple_toolkits_annotation_callgraph_CallGraphGrapher();}

    private MethodToContexts methodToContexts;
    private CallGraph cg;
    private boolean showLibMeths;
    
    private ArrayList getTgtMethods(SootMethod method){
        //System.out.println("meth for tgts: "+method);
        Body b = method.getActiveBody();
        ArrayList list = new ArrayList();
        Iterator sIt = b.getUnits().iterator();
        while (sIt.hasNext()){
            Stmt s = (Stmt) sIt.next();
            Iterator edges = cg.edgesOutOf(s);
            while (edges.hasNext()){
                Edge e = (Edge)edges.next();
                SootMethod sm = e.tgt();
                if (sm.getDeclaringClass().isLibraryClass()){
                    if (isShowLibMeths()){
                        list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm)));
                    }
                }
                else {
                    list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm)));
                }
            }
        }
        return list;
    }

    private boolean hasTgtMethods(SootMethod meth){
        ArrayList list = getTgtMethods(meth);
        if (!list.isEmpty()) return true;
        else return false;
    }

    private boolean hasSrcMethods(SootMethod meth){
        ArrayList list = getSrcMethods(meth);
        if (list.size() > 1) return true;
        else return false;
    }
    
    private ArrayList getSrcMethods(SootMethod method){
        System.out.println("meth for srcs: "+method);
        ArrayList list = new ArrayList();
        
        for( Iterator momcIt = methodToContexts.get(method).iterator(); momcIt.hasNext(); ) {
            final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
            Iterator callerEdges = cg.edgesInto(momc);
            while (callerEdges.hasNext()){
                Edge callEdge = (Edge)callerEdges.next();
                SootMethod methodCaller = callEdge.src();
                if (methodCaller.getDeclaringClass().isLibraryClass()){
                    if (isShowLibMeths()){
                        list.add(new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller)));
                    }
                }
                else {
                    list.add(new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller)));
                }
            } 
        }
        return list;
    }
    
    protected void internalTransform(String phaseName, Map options){
        
        CGGOptions opts = new CGGOptions(options);
        if (opts.show_lib_meths()){
            setShowLibMeths(true);
        }
        cg = Scene.v().getCallGraph();
        if (methodToContexts == null){
            methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
        }

        G.v().out.println("Running call graph grapher"); 
        if (Options.v().interactive_mode()){
            SootClass sc = Scene.v().getMainClass();
            SootMethod sm = getFirstMethod(sc);
            ArrayList tgts = getTgtMethods(sm);
            ArrayList srcs = getSrcMethods(sm);
            CallGraphInfo info = new CallGraphInfo(sm, tgts, srcs);
            InteractionHandler.v().handleCallGraphStart(info, this);
        }
    }

    private SootMethod getFirstMethod(SootClass sc){
        ArrayList paramTypes = new ArrayList();
        paramTypes.add(soot.ArrayType.v(soot.RefType.v("java.lang.String"), 1));
        if (sc.XdeclaresMethod("main", paramTypes, soot.VoidType.v())){
            return sc.XgetMethod("main", paramTypes, soot.VoidType.v());
        }
        else {
            return (SootMethod)sc.getMethods().get(0);
        }
    }
    
    public void handleNextMethod(){
        if (!getNextMethod().hasActiveBody()) return;
        ArrayList tgts = getTgtMethods(getNextMethod());
        //System.out.println("for: "+getNextMethod().getName()+" tgts: "+tgts);
        ArrayList srcs = getSrcMethods(getNextMethod());
        //System.out.println("for: "+getNextMethod().getName()+" srcs: "+srcs);
        CallGraphInfo info = new CallGraphInfo(getNextMethod(), tgts, srcs);
        //System.out.println("sending next method");
        InteractionHandler.v().handleCallGraphPart(info);
        //handleNextMethod();
    }
    
    private SootMethod nextMethod;

    public void setNextMethod(SootMethod m){
        nextMethod = m;
    }

    public SootMethod getNextMethod(){
        return nextMethod;
    }

    public void setShowLibMeths(boolean b){
        showLibMeths = b;
    }

    public boolean isShowLibMeths(){
        return showLibMeths;
    }

}


