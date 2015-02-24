#Plot settings
set term jpeg size 1920,1080 
set output "multipleExpDecay2dDecay0.2.jpeg"

set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"

set xrange [0:1] 

#Auxiliar Functions
max(x,y) = (x > y) ? x : y
mod(x) = (x<0) ? -x : x

#Score function parameters
decay = 0.2
scale = 0.2
offset = 0.1
ESValue(x, origin) = max(0., mod(x - origin) - offset)

Lambda = log(decay) / scale
expDecay(x, origin) = exp(Lambda*ESValue(x,origin)) 
magic = 1

set multiplot layout 2,5

set title "1"
unset key
originx = 1.0
set origin 0, 0
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.9"
unset key
originx = 0.9
set origin 0.2, 0
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.8"
unset key
originx = 0.8
set origin 0.4, 0
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.7"
unset key
originx = 0.7
set origin 0.6, 0
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.6"
unset key
originx = 0.6
set origin 0.8, 0
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.5"
unset key
originx = 0.5
set origin 0, .5
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.4"
unset key
originx = 0.4
set origin 0.2, .5
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.3"
unset key
originx = 0.3
set origin 0.4, .5
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.2"
unset key
originx = 0.2
set origin 0.6, .5
plot log(expDecay(x, originx)*magic*(1+originx))

set title "0.1"
unset key
originx = 0.1
set origin 0.8, .5
plot log(expDecay(x, originx)*magic*(1+originx))

unset multiplot
