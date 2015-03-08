setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study5\\data")
sink("rscript-wilcoxonRankSum.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
taskname="TaskName = Acc_neighbor_count"
taskname
Acc_neighbor_count_graph= c(accuracy1[,1])
Acc_neighbor_count_matrix= c(accuracy2[,1])
result = wilcox.test(Acc_neighbor_count_graph,Acc_neighbor_count_matrix)
cat(paste("Acc_neighbor_count", " , " , result$p.value, "\n" ))
sink()
