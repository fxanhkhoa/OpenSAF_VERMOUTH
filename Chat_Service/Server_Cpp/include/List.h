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
        Node(const char *k, const char *p, int _i = 0) : key(k, p, _i)
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
    void map_to_file(const char *file_name)
    {
        mu_lock_file.lock();
        fstream f(file_name, ios::out | ios::binary);
        f_map(f, root);
        f.close();
        mu_lock_file.unlock();
    }
    List(const char *file_name)
    {
        root = NULL;
        T tmp;
        mu_lock_file.lock();
        fstream f(file_name, ios::in | ios::binary);

        while (!f.eof())
        {
            f.read((char *)(&tmp), sizeof(T));
            insert(tmp);
        }
        f.close();
        mu_lock_file.unlock();
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
    void load(const char *file_name)
    {
        syslog(6, "vo load file");
        root = NULL;
        T tmp;
        //int len;
        mu_lock_file.lock();
        fstream f(file_name, ios::in | ios::binary);
        //FILE *f = fopen(file_name, "rb");

        syslog(6, "vo load file 1");
        while (!f.eof())
        {
            //  syslog(6, "vo load file 2");
            f.read((char *)(&tmp), sizeof(T));
            // fread(&tmp, sizeof(T), 1, f);
            insert(tmp);
        }
        f.close();
        //fclose(f);
        syslog(6, "vo load file 3");
        mu_lock_file.unlock();
    }
    int get_max_ID()
    {
        struct h
        {
            static int get(Node *r)
            {
                if (r)
                {
                    if (r->right)
                    {
                        return get(r->right);
                    }
                    else
                        return r->key.get_id();
                }
            }
        };
        return h::get(root);
    }
};
