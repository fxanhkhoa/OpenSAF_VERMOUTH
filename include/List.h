#pragma once
#include "user.h"
#include <vector>
#include <syslog.h>
using namespace std;
extern mutex mu_lock_file;
template <class T>
class List
{
    struct Node
    {
        T key;
        Node *left, *right;
        Node()
        {
            left = right = NULL;
        }
        Node(T &k) : key(k)
        {
            left = right = NULL;
        }
        Node(const char *k, const char *p, int _i) : key(k, p, _i)
        {
            left = right = NULL;
        }
        Node(const char *k, const char *p) : key(k, p)
        {
            left = right = NULL;
        }
    };
    Node *root;
    void f_map(fstream &f, Node *r)
    {
        if (r)
        {

            f.write((char *)(&(r->key)), sizeof(T));
            f_map(f, r->left);
            f_map(f, r->right);
        }
    }

  public:
    List()
    {
        root = NULL;
    }
    bool empty()
    {
        return !root;
    }
    T *insert(T key)
    {
        Node *p = root, *q = NULL;
        int f;
        while (p != NULL)
        {
            if (key == p->key)
                return NULL;
            q = p;
            if (key < p->key)
                f = 1, p = p->left;
            else
                f = 0, p = p->right;
        }
        Node *tmp = new Node(key);

        if (q == NULL)
        {
            root = tmp;
        }

        else
        {
            if (f)
                q->left = tmp;
            else
                q->right = tmp;
        }
        return &(tmp->key);
    }
    T *insert(const char *key, const char *pass)
    {
        Node *p = root, *q = NULL;
        int f, x;
        while (p != NULL)
        {
            x = strcmp(key, p->key.get_key());
            if (x == 0)
                return NULL;
            q = p;
            if (x < 0)
                f = 1, p = p->left;
            else
                f = 0, p = p->right;
        }
        Node *tmp = new Node(key, pass);

        if (q == NULL)
        {
            root = tmp;
        }

        else
        {
            if (f)
                q->left = tmp;
            else
                q->right = tmp;
        }
        return &(tmp->key);
    }
    T *insert(const char *key, const char *pass, int _id)
    {
        Node *p = root, *q = NULL;
        int f, x;
        while (p != NULL)
        {
            x = strcmp(key, p->key.get_key());
            if (x == 0)
                return NULL;
            q = p;
            if (x < 0)
                f = 1, p = p->left;
            else
                f = 0, p = p->right;
        }
        Node *tmp = new Node(key, pass, _id);

        if (q == NULL)
        {
            root = tmp;
        }

        else
        {
            if (f)
                q->left = tmp;
            else
                q->right = tmp;
        }
        return &(tmp->key);
    }
    T *search(const char *k)
    {
        Node *p = root;
        int x;
        while (p != NULL)
        {
            x = strcmp(k, p->key.get_key());
            if (x == 0)
                return &p->key;
            if (x < 0)
                p = p->left;
            else
                p = p->right;
        }
        return NULL;
    }

    T *search(int k)
    {
        Node *p = root;

        while (p != NULL)
        {

            if (k == p->key.get_key())
                return &p->key;
            if (k < p->key.get_key())
                p = p->left;
            else
                p = p->right;
        }
        return NULL;
    }
   

    void remove(T k)
    {
        Node *p = root, *q = NULL, *n, *c;
        while (p)
        {
            if (p->key == k)
                break;
            q = p;
            if (k < p->key)
                p = p->left;
            else
                p = p->right;
        }
        if (p == NULL)
            return;
        if (p->left && p->right)
        {
            n = p;
            q = p;
            p = p->left;
            while (p->right)
            {
                q = p;
                p = p->right;
            }
            n->key = p->key;
        }
        if (p->left)
            c = p->left;
        else
            c = p->right;
        if (p == root)
            root = c;
        else
        {
            if (q->left == p)
                q->left = c;
            else
                q->right = c;
        }
        delete p;
    }
    void remove(T *k)
    {
        Node *p = root, *q = NULL, *n, *c;
        while (p)
        {
            if (p->key == *k)
                break;
            q = p;
            if (*k < p->key)
                p = p->left;
            else
                p = p->right;
        }
        if (p == NULL)
            return;
        if (p->left && p->right)
        {
            n = p;
            q = p;
            p = p->left;
            while (p->right)
            {
                q = p;
                p = p->right;
            }
            n->key = p->key;
        }
        if (p->left)
            c = p->left;
        else
            c = p->right;
        if (p == root)
            root = c;
        else
        {
            if (q->left == p)
                q->left = c;
            else
                q->right = c;
        }
        delete p;
    }
    void to_vector(vector<T> &vec)
    {
        struct h
        {
            void get(vector<T> &vec, Node *r)
            {
                if (r)
                {
                    vec.push_back(r->key);
                    get(vec, r->left);
                    get(vec, r->right);
                }
            }
        } xx;
        xx.get(vec, root);
    }
    void to_vector(vector<T *> &vec)
    {
        struct h
        {
            void get(vector<T *> &vec, Node *r)
            {
                if (r)
                {
                    vec.push_back(&(r->key));
                    get(vec, r->left);
                    get(vec, r->right);
                }
            }
        } xx;
        xx.get(vec, root);
    }
    
    int get_max_ID()
    {
        struct h
        {
            static void get(Node *r,int &m)
            {
                if (r)
                {
                    get(r->left, m);
                    get(r->right, m);
                    int mm = r->key.get_id();
                    if (mm > m)
                        m = mm;
                }
            }
        };
        int res = 0;
        return h::get(root, res), res;
    }
};
