package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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

/**
 * Represents the signature attribute used by fields, methods and classes for dealing with generics in Java 1.5.
 */
public class SignatureTag implements Tag {

  public static final String NAME = SignatureTag.class.getSimpleName();

  private final String signature;

  public SignatureTag(String signature) {
    this.signature = signature;
  }

  public String getSignature() {
    return signature;
  }

  /** Returns the tag name. */
  @Override
  public String getName() {
    return NAME;
  }

  /** Returns the tag raw data. */
  @Override
  public byte[] getValue() {
    throw new RuntimeException(NAME + " has no value for bytecode");
  }

  public String getInfo() {
    return "Signature";
  }

  @Override
  public String toString() {
    return "Signature: " + signature;
  }
}
