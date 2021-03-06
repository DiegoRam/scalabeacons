#Plot settings
set term jpeg
set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"
set grid ztics lt 0 lw 1 lc rgb "#bbbbbb"

set zrange [0:1.5]
set yrange [0:1]
set xrange [0:1] 

set pm3d
set hidden3d

#Auxiliar Functions
max(x,y) = (x > y) ? x : y
mod(x) = (x<0) ? -x : x

#Score function parameters
decay = 0.5
scale = 0.2
offset = 0.1
s = scale / (1.0 - decay)

ESValue(x, origin) = max(0., mod(x - origin) - offset)
linearDecay(x, origin) = max(0., (s - ESValue(x, origin)) / s) 

#Plot
splot linearDecay(x, originx) * linearDecay(y, originy) 
