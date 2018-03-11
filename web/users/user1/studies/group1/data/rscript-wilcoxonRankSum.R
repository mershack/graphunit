setwd("C:\\Users\\Mershack\\Documents\\NetbeansProjects\\GitHub_projects\\d3graphevaluation\\build\\web\\users\\user1\\studies\\group1\\data")
sink("wilcoxon-rank-sum-analysis.txt")
accuracy1 = read.csv("AccuracyResults1.txt")
accuracy2 = read.csv("AccuracyResults2.txt")
time1 = read.csv("TimeResults1.txt")
time2 = read.csv("TimeResults2.txt")
cat(paste("\nAccuracy-Taskname ," ,"p-value," , "Effect-Size (r=z/sqrt(n))"))
cat("\n--------------------------------------------------------------")
Acc_selectNodeWithHighestDegree2_NL= c(accuracy1[,1])
Acc_selectNodeWithHighestDegree2_MAT= c(accuracy2[,1])
result = wilcox.test(Acc_selectNodeWithHighestDegree2_NL,Acc_selectNodeWithHighestDegree2_MAT)
size1 = length(c(accuracy1[,1]))
size2 = length(c(accuracy2[,1]))
num = result$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
es = num/denom
cat(paste("\nAcc_selectNodeWithHighestDegree2", " , " , result$p.value, " , ", abs(es) , "\n" ))
Acc_selectAllNeigbhorsOf1Node_NL= c(accuracy1[,2])
Acc_selectAllNeigbhorsOf1Node_MAT= c(accuracy2[,2])
result = wilcox.test(Acc_selectAllNeigbhorsOf1Node_NL,Acc_selectAllNeigbhorsOf1Node_MAT)
size1 = length(c(accuracy1[,2]))
size2 = length(c(accuracy2[,2]))
num = result$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
es = num/denom
cat(paste("\nAcc_selectAllNeigbhorsOf1Node", " , " , result$p.value, " , ", abs(es) , "\n" ))
Acc_selectMoreInterconnectedCluster2Clusters_NL= c(accuracy1[,3])
Acc_selectMoreInterconnectedCluster2Clusters_MAT= c(accuracy2[,3])
result = wilcox.test(Acc_selectMoreInterconnectedCluster2Clusters_NL,Acc_selectMoreInterconnectedCluster2Clusters_MAT)
size1 = length(c(accuracy1[,3]))
size2 = length(c(accuracy2[,3]))
num = result$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
es = num/denom
cat(paste("\nAcc_selectMoreInterconnectedCluster2Clusters", " , " , result$p.value, " , ", abs(es) , "\n" ))
cat(paste("\n\n\nTime-Taskname ," ,"p-value," , "Effect-Size (r=z/sqrt(n))"))
cat("\n--------------------------------------------------------------")
taskname="TaskName = Time_selectAllNeigbhorsOf1Node"
Time_selectAllNeigbhorsOf1Node_NL= c(time1[,2])
Time_selectAllNeigbhorsOf1Node_MAT= c(time2[,2])
result = wilcox.test(Time_selectAllNeigbhorsOf1Node_NL,Time_selectAllNeigbhorsOf1Node_MAT)
size1 = length(c(time1[,2]))
size2 = length(c(time2[,2]))
num = result$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
es = num/denom
cat(paste("\nTime_selectAllNeigbhorsOf1Node", " , " , result$p.value, " , ", abs(es) , "\n" ))
taskname="TaskName = Time_selectMoreInterconnectedCluster2Clusters"
Time_selectMoreInterconnectedCluster2Clusters_NL= c(time1[,3])
Time_selectMoreInterconnectedCluster2Clusters_MAT= c(time2[,3])
result = wilcox.test(Time_selectMoreInterconnectedCluster2Clusters_NL,Time_selectMoreInterconnectedCluster2Clusters_MAT)
size1 = length(c(time1[,3]))
size2 = length(c(time2[,3]))
num = result$statistic - (size1*size2)/2
denom = sqrt(((size1*size2) * (size1+size2+1))/12)
es = num/denom
cat(paste("\nTime_selectMoreInterconnectedCluster2Clusters", " , " , result$p.value, " , ", abs(es) , "\n" ))
sink()
