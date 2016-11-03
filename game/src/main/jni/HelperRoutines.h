/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#pragma once

#include "Common.h"

namespace mt
{

float slope(cv::Vec4i points);
void bwEdgeDetection(const cv::Mat& srcImage, cv::Mat& dstImage, bool useHls = false, double lowerThres = 3.f, double upperThresh = 252.f);

void inHsvRange(const cv::Mat& srcImage, const hsvPlanes& desc, cv::Mat& dstImage);
void inHsvRange(const cv::Mat& srcImage, const colorDescription& desc, cv::Mat& dstImage);
void inHsvRange(const cv::Mat& srcImage, cv::Scalar lowThres, cv::Scalar highThres, cv::Mat& dstImage);

void rectToRelative(const cv::Mat& mapping, const cv::Rect& input_rect, cv::Rect2f& output_rect);
void rectToAbsolute(const cv::Mat& mapping, const cv::Rect2f& input_rect, cv::Rect& output_rect);

void morphOps(cv::Mat& binaryImage);

}
