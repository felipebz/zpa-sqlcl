# ZPA-SQLcl

## Setup

Run the `configure.sh` script included which find `sql` in the path and populates the SQLCL\_HOME, SQLCL\_BIN, and SQLCL\_VERSION to be used by the maven build process

### Maven Repository

The script will install the SQLcl libraries to the local maven repository.

The configure script also creates a `sqlcl.properties` file which contains the properties used later by maven to deploy the custom command to SQLcl installation:

	sqlcl.bin=../sqlcl/bin
	sqlcl.home=../sqlcl
	sqlcl.version=18.4.0.0	
	

## Build

`mvn install` will install the code and it's dependencies into $SQLCL_HOME/lib/ext.



