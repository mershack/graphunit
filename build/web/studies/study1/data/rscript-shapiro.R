setwd("/disk/vizlab/apps/apache-tomcat-7.0.52/webapps/graphunit/studies/study6/data")
sink("rscript-shapiro.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
Acc_neighbor_count_graph= c(accuracy1[,1])
Acc_neighbor_count_matrix= c(accuracy2[,1])
taskname="TaskName = Acc_neighbor_count"
taskname
combineddata =data.frame(cbind(Acc_neighbor_count_graph,Acc_neighbor_count_matrix))
result=lapply(combineddata, shapiro.test)
paste("Acc_neighbor_count_graph ," , result$Acc_neighbor_count_graph$p.value)
paste("Acc_neighbor_count_matrix ," , result$Acc_neighbor_count_matrix$p.value)
"*********************"
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
Time_neighbor_count_graph= c(time1[,1])
Time_neighbor_count_matrix= c(time2[,1])
taskname="TaskName = Time_neighbor_count"
taskname
combineddata =data.frame(cbind(Time_neighbor_count_graph,Time_neighbor_count_matrix))
result=lapply(combineddata, shapiro.test)
paste("Time_neighbor_count_graph ," , result$Time_neighbor_count_graph$p.value)
paste("Time_neighbor_count_matrix ," , result$Time_neighbor_count_matrix$p.value)
"*********************"
sink()
