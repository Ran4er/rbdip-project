#!/usr/bin/env bash
set -euo pipefail

mvn -B clean verify
mvn -B org.pitest:pitest-maven:mutationCoverage
python3 scripts/write_score.py
cat score.json
