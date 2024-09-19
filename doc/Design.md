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

## 3. Syntax analysis

## 4. Error handling

## 5. Codegen

## 6. Test

### 6.1 Design principles

- Principle 1: good if nothing printed. Only print when something goes wrong, so that output files contains less info of no use.

- Principle 2: testing by incrementing; that is, testing a part of the basic functionalities, and use those already tested for the next testcase.