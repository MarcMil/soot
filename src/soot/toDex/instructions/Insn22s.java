package soot.toDex.instructions;

import java.util.BitSet;
import java.util.List;

import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Opcode;
import org.jf.dexlib.Code.Format.Instruction22s;

import soot.toDex.Register;

public class Insn22s extends AbstractInsn implements TwoRegInsn {
	
	private short litC;

	public Insn22s(Opcode opc, Register regA, Register regB, short litC) {
		super(opc);
		regs.add(regA);
		regs.add(regB);
		this.litC = litC;
	}
	
	public Register getRegA() {
		return regs.get(REG_A_IDX);
	}
	
	public Register getRegB() {
		return regs.get(REG_B_IDX);
	}
	
	public short getLitC() {
		return litC;
	}

	@Override
	protected Instruction getRealInsn0() {
		return new Instruction22s(opc, (byte) getRegA().getNumber(), (byte) getRegB().getNumber(), getLitC());
	}
	
	@Override
	public BitSet getIncompatibleRegs(List<Register> curRegs) {
		BitSet incompatRegs = new BitSet(2);
		if (!curRegs.get(REG_A_IDX).fitsByte()) {
			incompatRegs.set(REG_A_IDX);
		}
		if (!curRegs.get(REG_B_IDX).fitsByte()) {
			incompatRegs.set(REG_B_IDX);
		}
		return incompatRegs;
	}
	
	@Override
	public String toString() {
		return super.toString() + " lit: " + getLitC();
	}

	public Insn22s shallowCloneWithRegs(List<Register> newRegs) {
		Register newRegA = newRegs.get(REG_A_IDX);
		Register newRegB = newRegs.get(REG_B_IDX);
		Insn22s shallowClone = new Insn22s(getOpcode(), newRegA, newRegB, getLitC());
		shallowClone.setInsnOffset(getInsnOffset());
		return shallowClone;
	}
}