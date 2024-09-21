# CHISEL sandbox

Needs verilator to run the tests!

DUMP of verilator CLI for running tests

```bash
/usr/bin/verilator \
    '--cc' \
    '--exe' \
    '--build' \
    '-o' \
    '../simulation' \
    '--top-module' \
    'svsimTestbench' \
    '--Mdir' \
    'verilated-sources' \
    '--assert' \
    '-CFLAGS' \
    '-std=c++14 -I/tmp/chisel3.simulator.EphemeralSimulator_194496@localhost    localdomain_1844143133500783516/workdir-default -DSVSIM_ENABLE_VERILATOR_SUPPORT' \
    '../primary-sources/PassthroughGenerator.sv' 'testbench.sv' '../generated-sources/c-dpi-bridge.cpp' '../generated-sources/simulation-driver.cpp'
```

## TODO

- rocket chip
- RISCV BOOM
- hwacha
- chipyard
- verilator
