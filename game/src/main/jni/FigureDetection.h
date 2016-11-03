/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 */
 
#pragma once

#include "Common.h"

namespace mt
{

uint8_t locateFigure(const colorDescription& desc, const cv::Mat& srcimage, cv::Rect& rect);

}
