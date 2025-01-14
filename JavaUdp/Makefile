

  JAVA_FLAGS = -classpath ".;$(JAVA)/lib"
# LINT_FLAG  = -Xlint:deprecation

%.class : %.java
		javac $(LINT_FLAG) $<

% : %.class
		java $@

# ------------------------------------------------
all : trkSensor

# ------------------------------------------------
clean :
		rm -f *.class *~
