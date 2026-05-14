# Termination Tool for String Rewriting Systems

This project presents a termination analysis tool for **String Rewriting Systems (SRS)** using **SMT solving** and **polynomial interpretations**.

The tool automatically determines whether a rewriting system terminates by encoding termination constraints into SMT formulas and solving them with an SMT solver. Polynomial interpretations are used to construct well-founded orderings that prove termination.

## Features

* Automatic termination checking for String Rewriting Systems
* SMT-based constraint generation and solving
* Polynomial interpretation synthesis
* Human-readable proof/result formatting
* REST API support with Spring Boot
* Modular architecture for extending termination techniques

## Technologies

* Java
* Spring Boot
* Z3 SMT Solver
* Polynomial Interpretations
* RESTful API

## Overview

Given a set of rewrite rules, the tool:

1. Parses the rewriting system
2. Generates polynomial interpretation constraints
3. Encodes constraints into SMT formulas
4. Uses Z3 to search for satisfying interpretations
5. Reports whether termination can be proven

## Goal

The objective of this project is to combine the automation power of **SMT solving** with the theoretical strength of **polynomial interpretations** to provide an efficient and extensible framework for proving termination of string rewriting systems.
