setwd("/disk/vizlab/apps/apache-tomcat-7.0.52/webapps/graphunit/studies/study6/data")
sink("rscript-kruskalPostHoc.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
tasknames = NULL
pvalues = NULL
Acc_neighbor_count_graph= c(accuracy1[,1])
Acc_neighbor_count_matrix= c(accuracy2[,1])
combineddata =data.frame(Acc_neighbor_count_graph, Acc_neighbor_count_matrix)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata)
tasknames[1] = "Acc_neighbor_count_graph-Acc_neighbor_count_matrix"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_count_graph= c(time1[,1])
Time_neighbor_count_matrix= c(time2[,1])
combineddata =data.frame(Time_neighbor_count_graph, Time_neighbor_count_matrix)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata)
tasknames[1] = "Time_neighbor_count_graph-Time_neighbor_count_matrix"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
sink()
