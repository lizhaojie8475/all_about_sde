#include<iostream>
#include<vector>
using namespace std;

class unionFind {
    int num;
    vector<int> f;

public:
    unionFind(int n): num(n), f(vector<int>(this->num)) {
        for(int i = 0; i < num; i++){
            f[i] = i;
        }
    }

    int getFather(int x){
        return f[x] == x ? x : getFather(f[x]);
    }

    void merge(int src, int des){
        f[getFather(src)] = getFather(des);
    }

    bool query(int a, int b) {
        return getFather(a) == getFather(b);
    }
};