#Rscript plots_updated.r

op <- par(cex = 1.5)
par(mfrow=c(1,2))
a <- 8

pdf('rplot.pdf')

for(i in 1:1){

  m_cor <- 2
  M <- 20 #max number of drones
  m_seq <-seq(a, M, 1)
  a<-3
  #p_seq <- seq(0,0.5,0.001)
  p_seq <- seq(0,0.5,0.01)
  cor <- 0
  c_cor <- 1
  pass_cor <- 15


  #summation calculator. Return the probability for m.
  calc <- function(p,m,d){
    sum <- 0
    for(i in seq(d,m)){
      sum <- sum +  choose(m,i) * (1 - p)^(i-1) * (p) ^ (m-i)
    }
    return(sum)
  }


  ## The following changes reduce the number of points in the plot (and makes them much larger),
  ## so that it shows now that the domain is discrete, not continous
  ## I also changed cex.lab to 1.5, to enlarge the x and y axis labels
  plot(x = p_seq, ylim = c(1, 1.5), xlab = "p", ylab = "Fractional Gain", pch = NA, xaxt = 'n', cex.lab = 1.5, panel.first = grid())
  #axis(side=1, at= seq(0,1000,100), labels= seq(0,1,0.1), las=1, tck=- .02)
  axis(side=1, at= seq(0,1000,100), labels= seq(0,1,0.1), las=1, tck=- .02)

  for( m in m_seq ){

    r<-0
    c <- 1
    optmalError <- 0
    max <- 0
    d <- (m-1) / 2  + 1


   for (p in p_seq){

    if(m%%2 == 0){#even
        r[c] <- calc(p,m,m/2)
    }else{#odd
        r[c] <- calc(p,m,d)
    }

    if(max < r[c]){
      optmalError <- p #Optimal Error p
      max <- r[c]
    }
    c <- c + 1
    p <- r
   }#end_for

  #points(x = (1:length(r)), y =r, pch=1, cex =1,  col = colors()[m_cor+pass_cor])
  points(x = (1:length(r)), y =r, pch=1, cex =1,  col = colors()[m*pass_cor])##colors from 2*15 to max_m*15
  m_cor <- m_cor  + 2
  cor[c_cor] <- m+pass_cor
  c_cor <- c_cor + 1

  frac <- max(r) #Fractal Gain
  pm <- 1 - frac * (1 - optmalError) #Majority Error

  #print(paste("Number of Drones: ", m))
  print(paste("m = ",m, ", optimal value of p =", optmalError,", fractional gain =", frac, ", majority error (pm)=: ",  pm))

  }#end_for

  legend("topleft", legend= paste("m = ", m_seq),
  ## I changed cex to 0.9, to enlarge the text of the legends
  ##col= colors()[seq(17,170,2)], lty= 1, cex=0.9)
  col= colors()[seq(8*15,500,15)], lty= 1, cex=0.9)

}

dev.off()








