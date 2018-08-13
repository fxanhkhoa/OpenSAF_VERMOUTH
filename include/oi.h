#pragma once

#include "oiWrapper.h"

class Oi
{
    private:
        OiWrapper oiClass;
        std::string m_className;
    public:
        Oi(std::string implementerName, std::string className);
        ~Oi();
};

