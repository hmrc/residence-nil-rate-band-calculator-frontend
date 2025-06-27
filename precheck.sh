#!/bin/bash
sbt clean scalafmt test:scalafmt it/Test/scalfmt coverage it/test test coverageReport