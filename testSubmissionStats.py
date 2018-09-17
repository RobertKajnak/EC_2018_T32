# -*- coding: utf-8 -*-
"""

TestSubmission
Created on Sun Sep 16 20:56:01 2018

@author: Hesiris
"""
if __name__ == "__main__":
    import sys
    
    try:
        if len(sys.argv)>=2:
            runs_count=(int)(sys.argv[1])
        else:
            runs_count=38
            
        if len(sys.argv)>=3:
            dirOffset = sys.argv[2]
        else:
            dirOffset = ''
    except:
        print("Usage: testSubmissionStats.py [nr_runs] [testrun.jar location] . Default value = 38 <current_dircetory> \n")
        sys.exit()
    
    import subprocess
    
    #import os
    #currpath = os.path.dirname(os.path.abspath(__file__))
    resValTotal =0
    maxVal = -1
    minVal = 11
    resRTTotal =0
    maxRT = -1;
    
    print("Performing run: ",end='')
    for i in range(0,runs_count):
        print(i+1,end='', flush=True)
        
        result = subprocess.run(["java","-jar","testrun.jar","-submission=player32","-evaluation=BentCigarFunction","-seed=1"],
                                 universal_newlines=True,stderr=subprocess.PIPE,shell=True,stdout=subprocess.PIPE)
        
        indScore = result.stdout.find("Score:");
        resVal = (float)(result.stdout[indScore+7:indScore+13])
        resValTotal += resVal
        if resVal>maxVal:
            maxVal=resVal
        if resVal<minVal:
            minVal=resVal
        
        
        indRT = result.stdout.find("Runtime:");
        indRTend = result.stdout.find("ms");
        resRT = (float)(result.stdout[indRT+9:indRTend])
        resRTTotal += resRT
        if (resRT>maxRT):
            maxRT=resRT
        
        #I'm not importing math for this
        if i<runs_count-1:
            for j in range(0, 1 if i<9 else 2 if i<99 else 3):
                print('\b',end='')
        else:
            print('... Finished!')
        
        
    print("Avg. Runtime = {:5.2f}ms ; Worst run = {:5.2f}ms".format(resRTTotal/runs_count,maxRT))
    print("Avg. Result = {:5.4f}; Minimum: {:5.4f}; Maximum: {:5.4f}".format(resValTotal/runs_count,minVal,maxVal))
    
