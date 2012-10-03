package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Item;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction21c;

import soot.toDex.Register;

public class Insn21c extends AbstractInsn implements OneRegInsn {
	
	private Item<?> referencedItem;

	public Insn21c(Opcode opc, Register regA, Item<?> referencedItem) {
		super(opc);
		regs.add(regA);
		this.referencedItem = referencedItem;
	}

	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Item<?> getReferencedItem() {
		return referencedItem;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction21c(opc, (short) getRegA().getNumber(), referencedItem);
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(1);
		if (!curRegs.get(REG_A_IDX).fitsShort()) {
			incompatRegs.set(REG_A_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " ref: " + referencedItem;
	}

	public Insn21c shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Insn21c shallowClone = new Insn21c(getOpcode(), newRegA, referencedItem);
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}