# Design Documentation

[TOC]

## 0. Reference compiler: tolangc

### 0.1 Arch

### 0.2 Interface

### 0.3 File arch

## 1. General design

### 1.1 Arch

### 1.2 Interface

### 1.3 File arch

## 2. Lexical analysis

### 2.1 Initial design

The lexical analysis section of the compiler provides several modules: I/O handler, and the lexer itself.

| Class          | Function          | Description                                                  |
| -------------- | ----------------- | ------------------------------------------------------------ |
| frontend.Token | Datatype of token | Restore all information of a token, including token type `tokenId`, literal string `literal` and attributes `val`. |
| frontend.Lexer | Lexical analyzer  | Use a `BufferedReader` of `System.in` for input, and provide `read()` for fetching the next token. Each contains two loggers `stdout` and `stderr` generating output info in `read()` process. |
| io.Log         | Log output        | This is a reusable logger class. Contains a table of writers to put the same info to various output. Provide `addWriter`, `addFilewriter` and `configureWriter` for writer configurations. |

![design lexer init](./img/design_lexer_init.png)

### 2.2 Modified design

Further insight into the use of causes the following modification in design:

1. Cancel single instance design;

2. Seperate source of input, alter lexer input;

3. Add `lookAhead()` method and supporting mechanisms.

**The modification 1 & 2 is to enable a lexer to be reused during any period of compiling, as long as it is provided with a `Reader` or an input `String`.** This will combine with a reusable parser together to form a mechanism to "recompile" some code fragments when needed, e.x. when lowering a subtree of an AST.

The modicitication 3 is mainly to support the look-ahead operation, which 

## 3. Syntax anaysis

### Step 1: Basic syntax

Logic of parsing basic syntax into an AST.

### Step 2: Lowering advanced syntax

Add parsing logic for advanced syntax; then add AST transformation logic to transform AST including higher level syntax into a basic AST.

## 4. Semantic analysis

## 5. Error handling

## 6. Code gen

## 7. Tests

### 7.1 Design principles

- Principle 1: good if nothing printed. Only print when something goes wrong, so that output files contains less info of no use.

- Principle 2: testing by incrementing; that is, testing a part of the basic functionalities, and use those already tested for the next testcase.