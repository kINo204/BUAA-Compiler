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

Parse token on parser's call, or look ahead without advancing the cursor.

## 3. Syntax analysis

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