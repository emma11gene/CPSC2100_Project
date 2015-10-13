/**
 * Copyright 2015 jamiahx
 * jamiahx@gmail.com
 * 
 * This file is a part of CPSC2100_ORS.
 *
 * CPSC2100_ORS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.

 * CPSC2100_ORS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with CPSC2100_ORS.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.UUID;
import java.nio.charset.Charset;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public final class UUID_Generator
{
    private final UUID namespaceID;

    public static
	< DigestorT extends MessageDigest , BBT extends ByteBuffer >
	UUID generateUUID
	( UUID namespaceID , String name )
    {	byte[] namespaceIDBytes = 
	    BBT 
	    .allocateDirect(16)
	    .order( ByteOrder .BIG_ENDIAN )
	    .putLong( 0 ,
		      namespaceID .getMostSignificantBits()
		      )
	    .putLong( 8 ,
		      namespaceID .getLeastSignificantBits()
		      )
	    .array()
	    ;
	byte[] nameBytes =
	    BBT
	    .wrap(name
		  .getBytes( Charset
			     .forName( "UTF-8" )
			     )
		  )
	    .order( ByteOrder .BIG_ENDIAN )
	    .array()
	    ;
	byte[] concatenatedBytes = 
	    Arrays
	    .copyOf( namespaceIDBytes ,
		     namespaceIDBytes.length + nameBytes.length
		     )
	    ;
	System .arraycopy( nameBytes ,
			   0 ,
			   concatenatedBytes ,
			   namespaceIDBytes.length ,
			   nameBytes.length
			   )
	    ;
	byte[] hash =
	    DigestorT
	    .getInstance( "SHA-1" )
	    .digest( concatenatedBytes )
	    ;
	hash[ 7 ] =
	    ( hash[ 7 ]
	      & (byte) 0x0F
	    )
	    | (byte) 0x50
	    ;
	hash[ 8 ] =
	    ( hash[ 8 ]
	      & (byte) 0x3F
	    )
	    | (byte) 0x80
	    ;
	ByteBuffer hashBB =
	    BBT
	    .wrap(hash)
	    .order( ByteOrder .BIG_ENDIAN )
	    ;
	return new UUID(hashBB.getLong(0), hashBB.getLong(8))
	    ;
    }
    public UUID generateUUID( String name )
    {   return generateUUID( namespaceID , name )
	    ;
	    }

	public UUID_Generator( UUID namespaceID )
	    {
		this.namespaceID = namespaceID
		    ;
	    }
	public UUID_Generator( UUID prevNamespaceID , String namespaceName )
	    {
		namespaceID = generateUUID( prevNamespaceID , namespaceName )
		    ;
	    }
    }
