setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\users\\user1\\studies\\study2\\data")
sink("anova-PostHoc-Analysis.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
accuracy3 = read.csv("AccuracyResults3.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
time3 = read.csv("TimeResults3.txt")
tasknames = NULL
pvalues = NULL
tasknames = NULL
pvalues = NULL
tasknames = NULL
pvalues = NULL
tasknames = NULL
pvalues = NULL
Time_neighbor_two_step_arrow= c(time1[,2])
Time_neighbor_two_step_tapered= c(time2[,2])
results = t.test(Time_neighbor_two_step_arrow,Time_neighbor_two_step_tapered, paired=TRUE)
tasknames[1] = "Time_neighbor_two_step_arrow-Time_neighbor_two_step_tapered"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_two_step_arrow= c(time1[,2])
Time_neighbor_two_step_circular= c(time3[,2])
results = t.test(Time_neighbor_two_step_arrow,Time_neighbor_two_step_circular, paired=TRUE)
tasknames[1] = "Time_neighbor_two_step_arrow-Time_neighbor_two_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
tasknames = NULL
pvalues = NULL
Time_neighbor_two_step_tapered= c(time2[,2])
Time_neighbor_two_step_circular= c(time3[,2])
results = t.test(Time_neighbor_two_step_tapered,Time_neighbor_two_step_circular, paired=TRUE)
tasknames[1] = "Time_neighbor_two_step_tapered-Time_neighbor_two_step_circular"
pvalues[1] = results$p.value
pvalues_adj = p.adjust(pvalues, "bonferroni")
paste(tasknames[1] , "," , pvalues_adj[1])
sink()
