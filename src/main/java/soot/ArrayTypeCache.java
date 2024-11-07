package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Function;

import soot.Singletons.Global;

/**
 * Caches array types.
 * 
 * @author Marc Miltenberger
 */
public class ArrayTypeCache {
  private static class CacheEntry implements Comparable<CacheEntry> {
    private Type type;
    private int dimensions;

    public CacheEntry(Type t, int dimensions) {
      this.type = t;
      this.dimensions = dimensions;
    }

    @Override
    public int compareTo(CacheEntry o) {
      if (o == this) {
        return 0;
      }
      int hc1 = System.identityHashCode(this);
      int hc2 = System.identityHashCode(o);
      if (hc1 == hc2) {
        if (dimensions != o.dimensions) {
          return Integer.compare(dimensions, o.dimensions);
        } else {
          if (type.getNumber() != o.type.getNumber()) {
            return Integer.compare(type.getNumber(), o.type.getNumber());
          }
          return type.toString().compareTo(o.type.toString());
        }
      } else {
        return hc1 < hc2 ? -1 : 1;
      }
    }

    @Override
    public int hashCode() {
      return Objects.hash(dimensions, type);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      CacheEntry other = (CacheEntry) obj;
      return dimensions == other.dimensions && Objects.equals(type, other.type);
    }

  }

  //Initially we used a normal ConcurrentHashMap, however, it does not support
  //calling computeIfAbsent recursively 
  private final ConcurrentSkipListMap<CacheEntry, ArrayType> cache = new ConcurrentSkipListMap<>();

  private final Function<CacheEntry, ArrayType> mapping = new Function<CacheEntry, ArrayType>() {

    @Override
    public ArrayType apply(CacheEntry t) {
      final Type baseType = t.type;
      int numDimensions = t.dimensions;
      final int orgDimensions = numDimensions;
      Type elementType = baseType;
      while (numDimensions > 0) {
        ArrayType ret = elementType.getArrayType();
        if (ret == null) {
          int n = orgDimensions - numDimensions + 1;
          if (n != orgDimensions) {
            ret = cache.computeIfAbsent(new CacheEntry(baseType, n), mapping);
          } else {
            ret = new ArrayType(baseType, n);
          }
          elementType.setArrayType(ret);
        }
        elementType = ret;
        numDimensions--;
      }

      return (ArrayType) elementType;

    }

  };

  public ArrayTypeCache(Global g) {
  }

  /**
   * Returns a potentially cached array type of the given dimensions
   * @param baseType the base type (array element type)
   * @param numDimensions the number of dimensions
   * @return the array type
   */
  public ArrayType getArrayType(Type baseType, int numDimensions) {
    CacheEntry pairSearch = new CacheEntry(baseType, numDimensions);
    return cache.computeIfAbsent(pairSearch, mapping);

  }

}
