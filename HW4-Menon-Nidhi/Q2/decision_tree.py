from util import entropy, information_gain, partition_classes
import numpy as np
import ast
from scipy import stats

class DecisionTree(object):
    def __init__(self):
        # Initializing the tree as an empty dictionary or list, as preferred
        self.tree = None
        # self.tree = {}
        pass

    def learn(self, X, y):
        # TODO: Train the decision tree (self.tree) using the the sample X and labels y
        # You will have to make use of the functions in utils.py to train the tree

        # One possible way of implementing the tree:
        #    Each node in self.tree could be in the form of a dictionary:
        #       https://docs.python.org/2/library/stdtypes.html#mapping-types-dict
        #    For example, a non-leaf node with two children can have a 'left' key and  a
        #    'right' key. You can add more keys which might help in classification
        #    (eg. split attribute and split value)
        y = y.astype(np.int)
        self.tree = self.build_tree(X, y)

    # pass

    def build_tree(self, X, y):
        numerical_cols = set([1, 2, 7, 10, 13, 14, 15])

        if (np.all(y[0] == y[:])):
            return np.array([[int(-1), y[0], -1, -1]])
        info_gain_list = []

        for col in range(X.shape[1]):
            if col in numerical_cols:	#calculate mean for numerical values
                temp = X[:,col]
                temp1 = [float(a) for a in temp]
                temp_split_val = np.mean(temp1)
            else:			#calculate mode for categorical values
                temp_split_val = stats.mode(X[:,col])[0][0]

            y_new = []
            temp_X_left, temp_X_right, temp_y_left, temp_y_right = partition_classes(X, y, col, temp_split_val)
            y_new.append(temp_y_left)
            y_new.append(temp_y_right)
            ig = information_gain(y, y_new)

            info_gain_list.append(ig)

	#compute best feature to split on and the split_val
        best_feature = np.argmax(info_gain_list)
        if best_feature in numerical_cols:
            temp_best = X[:, best_feature].astype(float)
            split_val = np.mean(temp_best)
        else:
            split_val = stats.mode(X[:,best_feature])[0][0]

	#build sub-trees
        X_left, X_right, y_left, y_right = partition_classes(X, y, best_feature, split_val)
        left_tree = self.build_tree(np.array(X_left), np.array(y_left))
        right_tree = self.build_tree(np.array(X_right), np.array(y_right))
        root = np.array([[best_feature, split_val, 1, left_tree.shape[0] + 1]])
        x = np.append(root, left_tree, axis=0)
        result = np.append(x, right_tree, axis=0)
        return result

    def classify(self, record):
        # TODO: classify the record using self.tree and return the predicted label
        # pass
        x = 0

        numerical_cols = set([1, 2, 7, 10, 13, 14, 15])

        while (int(float(self.tree[x, 0])) != -1.0):
            feature = self.tree[x, 0]
            split_val = self.tree[x, 1]

            if feature in numerical_cols:
                #numerical values split on <= or > comparison
                if (record[int(float(feature))] <= float(split_val)):
                    x += int(float(self.tree[x, 2]))
                else:
                    x += int(float(self.tree[x, 3]))
            else:
                #categorcal values split on == or != comparison
                if(record[int(float(feature))]==split_val):
                    x += int(float(self.tree[x, 2]))
                else:
                    x += int(float(self.tree[x, 3]))

	#return predicted label as result
        res = int(float(self.tree[x, 1]))
        return res

