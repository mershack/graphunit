setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study5\\data")
sink("rscript-friedmanPostHoc.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
tasknames = NULL
pvalues = NULL
Acc_neighbor_one_step_edgesize2= c(accuracy1[,1])
Acc_neighbor_one_step_edgesize4= c(accuracy2[,1])
combineddata =data.frame(Acc_neighbor_one_step_edgesize2, Acc_neighbor_one_step_edgesize4)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Acc_neighbor_one_step_edgesize2-Acc_neighbor_one_step_edgesize4"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_one_step_edgesize2= c(time1[,1])
Time_neighbor_one_step_edgesize4= c(time2[,1])
combineddata =data.frame(Time_neighbor_one_step_edgesize2, Time_neighbor_one_step_edgesize4)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Time_neighbor_one_step_edgesize2-Time_neighbor_one_step_edgesize4"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
sink()
