//
// Created by huayang on 11/12/20.
//

#ifndef PROJ_ANDROID_FILTER_H
#define PROJ_ANDROID_FILTER_H

#include <vector>
#include <memory>

namespace sk {

    class Filter {

    public :
        virtual void FilterBars(std::vector<double>& bars) = 0;
    };

    class MonsterCatFilter : public Filter {
    public:
        void FilterBars(std::vector<double>& bars) override;
    };

    typedef std::shared_ptr<Filter>              FilterPtr;
    typedef std::shared_ptr<MonsterCatFilter>    MonsterCatFilterPtr;

}
#endif //PROJ_ANDROID_FILTER_H
