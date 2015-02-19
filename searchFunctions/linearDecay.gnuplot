set term jpeg
set output "linearDecay2APandProd.jpeg"

max(x,y) = (x > y) ? x : y
mod(x) = (x<0) ? -x : x

decay = 0.5
scale = 0.2
offset = 0.1
origin = 1.0
s = scale / (1.0 - decay)

set grid ytics lt 0 lw 1 lc rgb "#bbbbbb"
set grid xtics lt 0 lw 1 lc rgb "#bbbbbb"
set grid ztics lt 0 lw 1 lc rgb "#bbbbbb"

set zrange [0:1.5]
set yrange [0:1]
set xrange [0:1] 

splot max(0., (s - mod(x - origin)) / s) * max(0., (s - mod(y - origin)) / s)
