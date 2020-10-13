#!/usr/bin/env bash

HOME_DIR="$(cd `dirname $0` && cd .. && pwd)"

JAR_SWT="${HOME_DIR}/lib/swt-linux/swt.jar"
JAR_SWINGX="${HOME_DIR}/lib/swingx-0.9.3.jar"
JAR_JMULE="${HOME_DIR}/jmule.jar"

CLASSPATH="${JAR_SWT}:${JAR_SWINGX}:${JAR_JMULE}"

java -Xmx128m -cp "${CLASSPATH}"  org.jmule.main.Main
