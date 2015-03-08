setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\studies\\study6\\data")
sink("rscript-friedmanPostHoc.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
tasknames = NULL
pvalues = NULL
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
combineddata =data.frame(Acc_neighbor_one_step_arrow, Acc_neighbor_one_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Acc_neighbor_one_step_arrow-Acc_neighbor_one_step_tapered"
pvalues[1] = results$p.value
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
combineddata =data.frame(Acc_neighbor_two_step_arrow, Acc_neighbor_two_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[2] = "Acc_neighbor_two_step_arrow-Acc_neighbor_two_step_tapered"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
paste(tasknames[2] , "," , pvalues_adj[2])
tasknames = NULL
pvalues = NULL
Acc_neighbor_one_step_arrow= c(accuracy1[,1])
Acc_neighbor_one_step_circular= c(accuracy3[,1])
combineddata =data.frame(Acc_neighbor_one_step_arrow, Acc_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Acc_neighbor_one_step_arrow-Acc_neighbor_one_step_circular"
pvalues[1] = results$p.value
Acc_neighbor_two_step_arrow= c(accuracy1[,2])
Acc_neighbor_two_step_circular= c(accuracy3[,2])
combineddata =data.frame(Acc_neighbor_two_step_arrow, Acc_neighbor_two_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[2] = "Acc_neighbor_two_step_arrow-Acc_neighbor_two_step_circular"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
paste(tasknames[2] , "," , pvalues_adj[2])
tasknames = NULL
pvalues = NULL
Acc_neighbor_one_step_tapered= c(accuracy2[,1])
Acc_neighbor_one_step_circular= c(accuracy3[,1])
combineddata =data.frame(Acc_neighbor_one_step_tapered, Acc_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Acc_neighbor_one_step_tapered-Acc_neighbor_one_step_circular"
pvalues[1] = results$p.value
Acc_neighbor_two_step_tapered= c(accuracy2[,2])
Acc_neighbor_two_step_circular= c(accuracy3[,2])
combineddata =data.frame(Acc_neighbor_two_step_tapered, Acc_neighbor_two_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[2] = "Acc_neighbor_two_step_tapered-Acc_neighbor_two_step_circular"
pvalues[2] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
paste(tasknames[2] , "," , pvalues_adj[2])
tasknames = NULL
pvalues = NULL
Time_neighbor_one_step_arrow= c(time1[,1])
Time_neighbor_one_step_tapered= c(time2[,1])
combineddata =data.frame(Time_neighbor_one_step_arrow, Time_neighbor_one_step_tapered)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Time_neighbor_one_step_arrow-Time_neighbor_one_step_tapered"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_one_step_arrow= c(time1[,1])
Time_neighbor_one_step_circular= c(time3[,1])
combineddata =data.frame(Time_neighbor_one_step_arrow, Time_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Time_neighbor_one_step_arrow-Time_neighbor_one_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_one_step_tapered= c(time2[,1])
Time_neighbor_one_step_circular= c(time3[,1])
combineddata =data.frame(Time_neighbor_one_step_tapered, Time_neighbor_one_step_circular)
combineddata = stack(combineddata)
results = wilcox.test(values~ind, combineddata, paired=TRUE)
tasknames[1] = "Time_neighbor_one_step_tapered-Time_neighbor_one_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
sink()
