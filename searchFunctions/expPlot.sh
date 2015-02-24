# Use. $sh plot.sh name.jpeg originx originy
# ej.  $sh plot.sh name.jpeg .8 .5
gnuplot -e "set output '$1'; originx = $2; originy = $3" expDecay.gnuplot && eog $1
