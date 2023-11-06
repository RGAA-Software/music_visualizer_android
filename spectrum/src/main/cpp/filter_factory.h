//
// Created by huayang on 11/12/20.
//

#ifndef PROJ_ANDROID_FILTERFACTORY_H
#define PROJ_ANDROID_FILTERFACTORY_H

#include "filter.h"

namespace sk {

    class FilterFactory {
    public:

        static MonsterCatFilterPtr CreateMonsterCatFilter() {
            return std::make_shared<MonsterCatFilter>();
        }
    };

}

#endif //PROJ_ANDROID_FILTERFACTORY_H
