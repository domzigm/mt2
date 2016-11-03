/*
 *
 * (c) domzigm 2016 - GPLv3
 * https://github.com/domzigm/mt
 * 
 */

#include "com_domzi_mt2_NativeInterface.h"

#include "Common.h"
#include "BoardRectification.h"
#include "DiceDetection.h"
#include "FigureDetection.h"
#include "RemoteDetection.h"
#include "HelperRoutines.h"

// Get Image (always)
// Search for remote (always)
// calculate rectification (every n-th frame)
// rectify image (always)
// check for dice (on request)
// check for figures (always)

// getRemote: getImage, searchRemote, return pressed button
// getDice: getImage, rectifyImage*, return dice
// getFigure: getImage, rectifyImage*, return figure

// every n-th call to rectifiyImage, recalculate the rectification

using namespace cv;
using namespace mt;

std::vector<Rect2f> areas;
std::vector<colorDescription> figures;
std::vector<colorDescription> remotes;

const BoardRectificationConfig boardRectificationDefault = { 0 };
BoardRectificationConfig boardRectificationConfig = boardRectificationDefault;
BoardRectification boardRectification(boardRectificationConfig);

const DiceDetectionConfig diceDetectionDefault = { 0 };
DiceDetectionConfig diceDetectionConfig = diceDetectionDefault;
DiceDetection diceDetection(diceDetectionConfig);

const Rect2f diceDetectionROIDefault = { 0.f, 0.f, 1.f, 1.f };
Rect2f diceDetectionROI = diceDetectionROIDefault;

const RemoteDetectionConfig remoteDetectionDefault = { 0 };
RemoteDetectionConfig remoteDetectionConfig = remoteDetectionDefault;
RemoteDetection remoteDetection(remoteDetectionConfig);

Mat g_grayImage;
Mat g_rgbaImage;
Mat rectifiedImage;

void captureImage(Mat& image)
{
	(void)image;
}

////////////////////////
////////////////////////

#define RECT_ARG_CNT		4u
#define SCALAR_ARG_CNT		6u
#define HSV_H_MAX			179u
#define HSV_SV_MAX			255u

#define ERR_OKAY			0l
#define ERR_COORD_INVALID	-1l
#define ERR_OUTOFRANGE		-1l
#define ERR_NO_BOARD		-1l
#define ERR_AREA_OVERLAP	-2l
#define ERR_AREA_NOTDEFINED -2l
#define ERR_FIGURE_NOAREA   -3l
#define ERR_FIGURE_NOTLOCATED -4l

enum {
	POS_H_LO = 0,
	POS_H_HI,
	POS_S_LO,
	POS_S_HI,
	POS_V_LO,
	POS_V_HI
};

JNIEXPORT void JNICALL Java_com_domzi_mt2_NativeInterface_updateImage
  (JNIEnv *env, jobject obj, jlong grayImage, jlong rgbaImage)
{
	((cv::Mat*)grayImage)->copyTo(g_grayImage);
  ((cv::Mat*)rgbaImage)->copyTo(g_rgbaImage);
}

JNIEXPORT jlong JNICALL Java_com_domzi_mt2_NativeInterface_getPreviewImage
  (JNIEnv * env, jobject obj)
{
  return (jlong)(&g_rgbaImage);
}

JNIEXPORT void JNICALL Java_com_domzi_mt2_NativeInterface_init
(JNIEnv *env, jobject obj)
{
	(void)env;
	(void)obj;
	boardRectificationConfig = boardRectificationDefault;
	remoteDetectionConfig = remoteDetectionDefault;
	diceDetectionConfig = diceDetectionDefault;
	diceDetectionROI = diceDetectionROIDefault;
	figures.clear();
	remotes.clear();
	areas.clear();
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_updateImage
(JNIEnv *env, jobject obj)
{
	(void)env;
	(void)obj;

	// Detect board markers
	if (boardRectification.updateBoard(g_rgbaImage)) {

		// Rectify the image
		boardRectification.rectifyImage(g_rgbaImage, rectifiedImage);
		return com_domzi_mt2_NativeInterface_RETURN_OK;
	}
	return ERR_NO_BOARD;
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_registerArea
(JNIEnv *env, jobject obj, jfloatArray arr)
{
	(void)obj;

	// Check if enough arguments are supplied
	if (RECT_ARG_CNT != env->GetArrayLength(arr)) {
		return ERR_COORD_INVALID;
	}
	
	// Get values
	auto vals = env->GetFloatArrayElements(arr, 0);

	// Check if one value is >100%
	if (vals[0] > 1.f || vals[1] > 1.f || vals[2] > 1.f || vals[3] > 1.f) {
		return ERR_COORD_INVALID;
	}

	// Check if x+width or y+height is >100%
	if ((vals[0] + vals[2]) > 1.f || (vals[1] + vals[2]) > 1.f) {
		return ERR_COORD_INVALID;
	}

	// Check for overlapping areas
	Rect2f newArea(vals[0], vals[1], vals[2], vals[3]);
	for (auto area : areas) {

		if ((area & newArea).area() > 0) {
			return ERR_AREA_OVERLAP;
		}
	}

	// Add area
	areas.push_back(newArea);

	return (jint)(areas.size() - 1);
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_setDiceRoi
(JNIEnv *env, jobject obj, jfloatArray arr)
{
	(void)obj;

	// Check if enough arguments are supplied
	if (RECT_ARG_CNT != env->GetArrayLength(arr)) {
		return ERR_COORD_INVALID;
	}

	// Get values
	auto vals = env->GetFloatArrayElements(arr, 0);

	// Check if one value is >100%
	if (vals[0] > 1.f || vals[1] > 1.f || vals[2] > 1.f || vals[3] > 1.f) {
		return ERR_COORD_INVALID;
	}

	// Check if x+width or y+height is >100%
	if ((vals[0] + vals[2]) > 1.f || (vals[1] + vals[2]) > 1.f) {
		return ERR_COORD_INVALID;
	}

	// Set new ROI
	Rect2f newROI(vals[0], vals[1], vals[2], vals[3]);
	diceDetectionROI = newROI;

	return com_domzi_mt2_NativeInterface_RETURN_OK;
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_registerRemote
(JNIEnv *env, jobject obj, jintArray arr)
{
	(void)obj;

	// Check if enough arguments are supplied
	if (SCALAR_ARG_CNT != env->GetArrayLength(arr)) {
		return ERR_OUTOFRANGE;
	}

	// Get values
	auto vals = env->GetIntArrayElements(arr, 0);

	// Range check values
	if (vals[POS_H_LO] < 0 || vals[POS_H_LO] > vals[POS_H_HI] || vals[POS_H_HI] > HSV_H_MAX) {
		return ERR_OUTOFRANGE;
	}
	if (vals[POS_S_LO] < 0 || vals[POS_S_LO] > vals[POS_S_HI] || vals[POS_S_HI] > HSV_SV_MAX) {
		return ERR_OUTOFRANGE;
	}
	if (vals[POS_V_LO] < 0 || vals[POS_V_LO] > vals[POS_V_HI] || vals[POS_V_HI] > HSV_SV_MAX) {
		return ERR_OUTOFRANGE;
	}

	// Create new figure description
	colorDescription desc;
	desc.lowerThreshold = Scalar(vals[POS_H_LO], vals[POS_S_LO], vals[POS_V_LO]);
	desc.upperThreshold = Scalar(vals[POS_H_HI], vals[POS_S_HI], vals[POS_V_HI]);
	remotes.push_back(desc);

	return (jint)(remotes.size() - 1);
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_getRemote
(JNIEnv *env, jobject obj, jint val)
{
	(void)env;
	(void)obj;

	// Check if index is available
	if (val >= remotes.size()) {
		return ERR_OUTOFRANGE;
	}

	jint retVal = -2l;
	uint8_t remoteStatus = 0;

	auto ret = remoteDetection.getRemoteStatus(remotes[val], rectifiedImage, remoteStatus);

	switch (ret)
	{
	case REMOTE_BUTTON_PRESSED:
		retVal = (jint)remoteStatus;
		break;
	case REMOTE_NOT_PRESSED:
		retVal = com_domzi_mt2_NativeInterface_REMOTE_NOT_PRESSED;
		break;
	case REMOTE_NOT_DETECTED:
	default:
		retVal = com_domzi_mt2_NativeInterface_REMOTE_NOT_DETECTED;
		break;
	}

	return remoteStatus;
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_registerFigure
(JNIEnv *env, jobject obj, jintArray arr)
{
	(void)obj;

	// Check if enough arguments are supplied
	if (SCALAR_ARG_CNT != env->GetArrayLength(arr)) {
		return ERR_OUTOFRANGE;
	}

	// Get values
	auto vals = env->GetIntArrayElements(arr, 0);

	// Range check values
	if (vals[POS_H_LO] < 0 || vals[POS_H_LO] > vals[POS_H_HI] || vals[POS_H_HI] > HSV_H_MAX) {
		return ERR_OUTOFRANGE;
	}
	if (vals[POS_S_LO] < 0 || vals[POS_S_LO] > vals[POS_S_HI] || vals[POS_S_HI] > HSV_SV_MAX) {
		return ERR_OUTOFRANGE;
	}
	if (vals[POS_V_LO] < 0 || vals[POS_V_LO] > vals[POS_V_HI] || vals[POS_V_HI] > HSV_SV_MAX) {
		return ERR_OUTOFRANGE;
	}

	// Create new figure description
	colorDescription desc;
	desc.lowerThreshold = Scalar(vals[POS_H_LO], vals[POS_S_LO], vals[POS_V_LO]);
	desc.upperThreshold = Scalar(vals[POS_H_HI], vals[POS_S_HI], vals[POS_V_HI]);
	figures.push_back(desc);

	return (jint)(figures.size() - 1);
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_getFigure
(JNIEnv *env, jobject obj, jint val)
{
	(void)env;
	(void)obj;

	// Check if index is available
	if (val >= figures.size()) {
		return ERR_OUTOFRANGE;
	}

	// Check if areas have been registered
	if (areas.size() == 0) {
		return ERR_AREA_NOTDEFINED;
	}

	// Locate the figure
	Rect figureRect;
	if (0 != locateFigure(figures.at(val), rectifiedImage, figureRect)) {
		
		// Convert the position to relative
		Rect2f relativeFigureRect;
		rectToRelative(rectifiedImage, figureRect, relativeFigureRect);

		// Iterate all areas
		jint index = 0;
		for (auto& area : areas) {
			
			// Return on intersection
			if ((area & relativeFigureRect).area() > 0) {
				return index;
			}
			index++;
		}
		return ERR_FIGURE_NOAREA;
	}
	return ERR_FIGURE_NOTLOCATED;
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_getDice
(JNIEnv *env, jobject obj)
{
	(void)env;
	(void)obj;

	uint32_t dice = 0;
	uint32_t eyes = 0;

	// Calculate the ROI based on the image size
	Rect2f roi;
	roi.x = rectifiedImage.cols * diceDetectionROI.x;
	roi.y = rectifiedImage.rows * diceDetectionROI.y;
	roi.width = rectifiedImage.cols * diceDetectionROI.width;
	roi.height = rectifiedImage.rows * diceDetectionROI.height;

	Mat roiImage = rectifiedImage(roi);
	diceDetection.processImage(roiImage);
	diceDetection.getResult(dice, eyes);
	return (jint)dice;
}

JNIEXPORT jint JNICALL Java_com_domzi_mt2_NativeInterface_getEyes
(JNIEnv *env, jobject obj)
{
	(void)env;
	(void)obj;

	uint32_t dice = 0;
	uint32_t eyes = 0;
	diceDetection.getResult(dice, eyes);
	return (jint)eyes;
}
