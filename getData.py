import numpy as np
from subprocess import check_output
import sys
import re
import csv

from random import randint, uniform

def run():
    cmd = ["java", "-jar", "testrun.jar", "-submission=player32", "-evaluation=KatsuuraEvaluation", "-seed={}".format(randint(0, 15615661))] 

    # only for Linux users
    cmd = [' '.join(cmd)]

    p = check_output(cmd, shell=True)

    return p

def main():
    num_runs = 300
    output = []
    max_number_of_evaluations = 1e6
    
    for i in range(num_runs):
        print("run: {}".format(i+1))
        output.append(run())
    
    # print(output)
    info = {
        "Island_1A" : {},
        "Island_2A" : {},
        "Island_1B" : {},
        "Island_2B" : {},
        "Island_3A" : {},
        "Island_3B" : {},
    }

    for key in info.keys():
        for i in range(num_runs):
            pattern_metrics = r"(?<=" + key + r" - )(?:.(?!n\\t))*".format(key)
            island_info = re.findall(pattern_metrics, str(output[i]))

            if len(info[key].keys()) == 0:
                info[key]['performance'] = []
                info[key]['diversity'] = []
                info[key]['evaluations'] = []
                info[key]['best'] = []
            info[key]["performance"].append([float(i.split(',')[0]) for i in island_info])
            info[key]["diversity"].append([float(i.split(',')[1].split()[-1]) for i in island_info])
            info[key]["evaluations"].append([float(i.split(',')[2].split()[-1]) for i in island_info])
            info[key]["best"].append([[0, 1][i.split(',')[3].split()[-1] == "yes"] for i in island_info])

            # print(info[key]["performance"])
            # print(info[key]["diversity"])
            # print(info[key]["best"])

    for key in info.keys():
        island_info = info[key]

        # make all list of the same length:
        l = min([len(x) for x in island_info["performance"]])

        for metric in ['performance', 'diversity', 'evaluations', 'best']:
            island_info[metric] = [i[:l] for i in island_info[metric]]

        performance = np.array(island_info["performance"])
        diversity = np.array(island_info["diversity"])
        evaluations = np.array(island_info["evaluations"])
        best = np.array(island_info["best"])

        print(performance)
        mean_performance = np.mean(performance, axis=0)
        std_performance = np.std(performance, axis=0)

        mean_diversity = np.mean(diversity, axis=0)
        std_diversity = np.std(diversity, axis=0)

        mean_evaluations = np.mean(evaluations, axis=0)
        std_evaluations = np.std(evaluations, axis=0)

        mean_best = np.mean(best, axis=0)
        std_best = np.std(best, axis=0)

        island_info["mean_performance"] = mean_performance
        island_info["std_performance"] = std_performance
        island_info["mean_diversity"] = mean_diversity
        island_info["std_diversity"] = std_diversity
        island_info["mean_evaluations"] = mean_evaluations
        island_info["std_evaluations"] = std_evaluations
        island_info["mean_best"] = mean_best
        island_info["std_best"] = std_best

        # island_info["takeover_time"] = []
        # # compute the takeover time
        # for i in range(num_runs):
        #     perf_idx = -2
        #     print(island_info["performance"])
        #     last_performance = island_info["performance"][i][perf_idx+1]
        #     curr_performance = island_info["performance"][i][perf_idx]
        #     while abs(curr_performance - last_performance) < 1e-3:
        #         perf_idx -= 1
        #         print(curr_performance - last_performance)
        #         last_performance = curr_performance
        #         curr_performance = island_info["performance"][i][perf_idx]

        for metric in ['performance', 'diversity', 'evaluations', 'best']:
            with open('Results/{}_{}.csv'.format(key, metric), 'w') as f:
                writer = csv.writer(f, delimiter=',',)
                for mean,std in zip(island_info["mean_{}".format(metric)], island_info["std_{}".format(metric)]):
                    writer.writerow([mean, std])

if __name__ == "__main__":
    main()

