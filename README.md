# minicoc
A minimal implementation of the [calculus of constructions](https://en.wikipedia.org/wiki/Calculus_of_constructions), capable of proving intricate theorems such as `1 + 1 == 2`.

## Quick Start
```console
$ ./gradlew b
$ ./gradlew r --args="examples/one-plus-one.coc"
```

## Syntax
There are two types of statements:
```
<id> = <term>
<term> : <term>
```
The former binds the name `<id>` to the term given on the right-hand side. The latter requests a type-check and errors if the type of the left-hand side is not equal to the term given by the right-hand side.

A line that begins with whitespace is treated as a continuation of the previous statement. A line that does not begin with whitespace is treated as the beginning of a new statement.
Comments begin with `--` and last until the next newline character.

An informal grammar for terms is given below:
```
-- Sorts
Prop
Type 

-- Variables
<id>

-- Products
Π(<id>: <term>). <term>

-- Abstraction
λ(<id>: <term>). <term>

-- Application
<term> <term> -- left associative
```
To avoid Girard's paradox (the type-theory version of Russell's paradox), the type of `Type` may not be inferred. More complicated systems have a hierarchy of types (see the Calculus of Inductive Types), but this minimal implementation of CoC does not.

For notational convenience, the following syntax is also provided:
```
-- Easier-to-type
Pi(<id>: <term>). <term> -- equivalent to λ
Lam(<id>: <term>). <term> -- equivalent to Π

-- Higher-arity lambdas
λ(<id>: <term>) (<id>: <term>) ... (<id>: <term>). <term>
-- equivalent to λ(<id>: <term>). λ(<id>: term>). ... λ(<id>: <term>). <term>
-- also possible with Lam, Π, Pi

-- Implication 
<term> → <term>
<term> ⇒ <term>
<term> -> <term>
<term> => <term>
-- all equivalent
-- right associative
```
