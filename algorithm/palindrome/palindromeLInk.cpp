#include<iostream>
#include<vector>

using namespace std;
/*
判断回文系列题是最简单的一类回文题目，只需要判断传入的数据结构是否符合回文性质即可。这类题目无论传入的数据结构是什么类型，都按照一个思想，就是想办法从前后两端同时遍历，直到前后遍历的指针相遇为止，元素值
都相同。用最简单的数组或者字符串来说，就直接用两个指针分别指向头部和尾部，依次向内收拢，直到遇到不相同的就跳出循环即可。对于这种判断型题目，唯一麻烦的就是对链表结构的判断，由于没办法直接从后向前遍历。

其实单纯的对链表进行反向遍历也并不困难，只需要用递归的方式完成即可（其实本质就是后续遍历，即把一个操作写在递归函数执行之后再执行，这个操作就会全局反向执行）。但是如果需要两端同时进行的话，就必须用全局变量
记录正向的指针。

*/
struct Node{
    int val;
    Node* next;
    Node(int v): val(v), next(nullptr){}
};

Node* left;
bool isPalindrome(Node* head){
    if(head == nullptr || head->next == nullptr)
        return true;
    left = head;

    return true;
}

bool traverse(Node* right){
    if(right == nullptr)
        return true;
    bool res = traverse(right->next)
    //后序遍历位置，只要在这个位置之后写的代码，所读到的right指针就都是倒序排列的。
    res = (res && (left->val == right->val));
    left = left->next;
    return res;
}


int main() {


}