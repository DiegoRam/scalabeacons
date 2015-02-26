#Plot settings
set term jpeg 

#Uncomment if it is not using plot.sh
#set output "img.jpeg"

set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"
set grid ztics lt 0 lw 1 lc rgb "#bbbbbb"

#x and y axis range
set yrange [0:1]
set xrange [0:1] 

#Uncomment to draw with color
set pm3d
set hidden3d

#Auxiliar Functions
max(x,y) = (x > y) ? x : y
mod(x) = (x<0) ? -x : x

#function parameters
decay = 0.5
scale = 0.2
offset = 0.1

#Auxilar Functions
ESValue(x, origin) = max(0., mod(x - origin) - offset)
Lambda = log(decay) / scale
s = scale / (1.0 - decay)
sigma = -scale*scale / (2*log(decay))

gaussDecay(x, origin) = exp(-ESValue(x, origin)/(2*sigma))
linearDecay(x, origin) = max(0., (s - ESValue(x, origin)) / s) 
expDecay(x, origin) = exp(Lambda*ESValue(x,origin)) 

splot log(expDecay(x, originx)*(1+originx)) + log(expDecay(y, originy)*(1+originy))
