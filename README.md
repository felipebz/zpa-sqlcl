# ZPA-SQLcl

**This is a work in progress...**

This repo contains an extension to run static code analysis using the [Z PL/SQL Analyzer](https://github.com/felipebz/zpa) inside [Oracle SQLcl](https://www.oracle.com/database/technologies/appdev/sqlcl.html).

## How it works

- Enable the static analysis:

```
SQL> zpa on
Static analysis enabled
```

- Write you code as normal:

```
SQL> declare
  2    v varchar2(1);
  3  begin
  4    if v = '' then
  5      null;
  6    elsif v in ('a', 'b', 'a') then
  7      null;
  8    end if;
  9  end;
 10  /

PL/SQL procedure successfully completed.
```

- After the execution you'll see a report from ZPA:

```
Line 4: Fix this comparison or change to "IS NULL".
Line 6: Remove or fix the duplicated value "'a'" in the IN condition.
Line 7: Either merge this branch with the identical one on line 5 or change one of the implementations.
```

## Setup

Run the `configure.sh` script included in this repository to install the SQLcl libraries to the local maven repository.

The configure script also creates a `sqlcl.properties` file which contains the properties used later by maven to deploy the custom command to SQLcl installation:

	sqlcl.bin=../sqlcl/bin
	sqlcl.home=../sqlcl
	sqlcl.version=19.2.1.0	
	

Then run `mvn install` to install this plugin into your SQLcl home or run `mvn package` and manually copy `target/zpa-sqlcl-0.0.1-SNAPSHOT.jar` to $SQLCL_HOME/lib/ext .



