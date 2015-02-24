#Plot settings
set term jpeg size 1920,1080 

set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"
set grid ztics lt 0 lw 1 lc rgb "#bbbbbb"

#set zrange [0:1.5]
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
ESValue(x, origin) = max(0., mod(x - origin) - offset)

Lambda = log(decay) / scale
s = scale / (1.0 - decay)

linearDecay(x, origin) = max(0., (s - ESValue(x, origin)) / s) 
expDecay(x, origin) = exp(Lambda*ESValue(x,origin)) 
magic = 23

splot log(expDecay(x, originx)*magic*(1+originx)) + log(expDecay(y, originy)*magic*(1+originy))
