"use client";

import Image from "next/image";
import { useId, useState, useTransition } from "react";

import { ConfirmModal } from "@/components/ui/ConfirmModal";
import { toast } from "@/components/ui/Toast";
import { CareRecordResponseDto, GrowthRecordResponseDto } from "@/lib/api-client/gen";
import { formatJapaneseDateBasic, formatJapaneseDateTimeOnly } from "@/utils/date";

import { deleteCareRecordAction } from "../actions/deleteCareRecordAction";
import { deleteGrowthRecordAction } from "../actions/deleteGrowthRecordAction";
import { updateCareRecordAction } from "../actions/updateCareRecordAction";
import { updateGrowthRecordAction } from "../actions/updateGrowthRecordAction";
import diaperIcon from "../assets/diaper.svg";
import growthIcon from "../assets/growth.svg";
import healthIcon from "../assets/health.svg";
import mealIcon from "../assets/meal.svg";
import milkIcon from "../assets/milk.svg";
import { UpdateDataParams } from "../types";
import { generateUpdateCareRecordActionParams } from "../utils/generateParams";
import { validateCareRecordUpdate, validateGrowthRecordUpdate } from "../utils/validation";

type Props =
  | { index: number; careItem: CareRecordResponseDto; growthItem?: never }
  | { index: number; growthItem: GrowthRecordResponseDto; careItem?: never };

export const CareTimeLineItem = ({ index, careItem, growthItem }: Props) => {
  const dataMap = {
    MEAL: { icon: mealIcon, label: "食事" },
    MILK: { icon: milkIcon, label: "ミルク" },
    DIAPER: { icon: diaperIcon, label: "排泄" },
    HEALTH: { icon: healthIcon, label: "体調" },
  };

  // 日時情報
  const dateData = careItem ? new Date(careItem.recordedAt) : new Date(growthItem!.measurementDate);

  // 更新データ管理
  const [updateData, setUpdateData] = useState<UpdateDataParams>({
    date: formatJapaneseDateBasic(dateData),
    time: formatJapaneseDateTimeOnly(dateData),
    note:
      careItem?.mealDetail?.note ||
      careItem?.milkDetail?.note ||
      careItem?.diaperDetail?.note ||
      careItem?.healthDetail?.note ||
      growthItem?.note ||
      "",
    amountMl: careItem?.milkDetail?.amountMl,
    diaperType: careItem?.diaperDetail?.diaperType,
    temperature: careItem?.healthDetail?.temperature,
    height: growthItem?.height,
    weight: growthItem?.weight,
  });

  // ラジオボタン用ID
  const uid = useId();

  // 編集モード管理
  const [isEditMode, setIsEditMode] = useState<boolean>(false);

  // 削除モーダル開閉フラグ
  const [isDeleteConfirmOpen, setIsDeleteConfirmOpen] = useState<boolean>(false);

  // 更新モーダル開閉フラグ
  const [isSaveConfirmOpen, setIsSaveConfirmOpen] = useState<boolean>(false);

  // 非同期処理中のボタン状態管理
  const [isPending, startTransition] = useTransition();

  // 更新処理
  const saveAction = async () => {
    startTransition(async () => {
      // 育児記録更新
      if (careItem) {
        // 詳細入力値バリデーション
        if (!validateCareRecordUpdate(careItem, updateData)) return;

        // 更新処理
        const result = await updateCareRecordAction(
          // 種別ごとに合わせたパラメータを作成
          generateUpdateCareRecordActionParams(careItem, updateData),
        );

        if (result.success) {
          setIsEditMode(false);
          setIsSaveConfirmOpen(false);
          toast.success("育児記録を更新しました");
        } else {
          toast.error(result.error);
        }
      }

      // 身長体重更新
      if (growthItem) {
        // validationチェック
        if (!validateGrowthRecordUpdate(updateData)) return;

        const result = await updateGrowthRecordAction({
          id: growthItem.id,
          measurementDate: new Date(updateData.date),
          height: updateData.height,
          weight: updateData.weight,
          note: updateData.note,
        });
        if (result.success) {
          setIsEditMode(false);
          setIsSaveConfirmOpen(false);
          toast.success("成長記録を更新しました");
        } else {
          toast.error(result.error);
        }
      }
    });
  };

  // 削除処理
  const deleteAction = async () => {
    startTransition(async () => {
      if (careItem) {
        const result = await deleteCareRecordAction({ id: careItem.id });

        if (result.success) {
          setIsDeleteConfirmOpen(false);
          toast.success("育児記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }

      if (growthItem) {
        const result = await deleteGrowthRecordAction({ id: growthItem.id });

        if (result.success) {
          setIsDeleteConfirmOpen(false);
          toast.success("成長記録の削除に成功しました");
        } else {
          toast.error(result.error);
        }
      }
    });
  };

  return (
    <div className="relative flex items-center gap-10 py-2.5 max-md:gap-6 max-md:py-2">
      <div
        className={`bg-brown-dark h-2 w-2 shrink-0 rounded-full ${growthItem ? "opacity-0" : ""}`}
      ></div>
      <div
        className={`bg-brown-dark absolute left-1 h-full w-px ${index === 0 ? "top-[50%]" : ""} ${growthItem ? "opacity-0" : ""}`}
      ></div>
      <div className="bg-background-light border-brown-dark w-full gap-5 rounded-lg border px-6 py-3 max-md:p-3">
        <div className="flex items-start gap-5">
          {/* アイコン表示 */}
          {careItem && (
            <Image
              src={dataMap[careItem.recordType].icon}
              alt=""
              className="max-md:h-12 max-md:w-12"
              width={60}
              height={60}
            />
          )}
          {growthItem && (
            <Image
              src={growthIcon}
              alt=""
              className="max-md:h-12 max-md:w-12"
              width={60}
              height={60}
            />
          )}

          {/* 表示モード時の詳細表示 */}
          {!isEditMode && (
            <div>
              {/* 身長/体重表示 */}
              {growthItem && (
                <>
                  {growthItem.height && (
                    <p className="font-medium max-md:text-sm">身長: {growthItem.height}cm</p>
                  )}
                  {growthItem.weight && (
                    <p className="font-medium max-md:text-sm">体重: {growthItem.weight}kg</p>
                  )}
                </>
              )}

              {/* 日時表示 */}
              {careItem && (
                <p className="text-xl font-medium max-md:text-sm">
                  {formatJapaneseDateTimeOnly(careItem.recordedAt)}
                </p>
              )}
              {/* 育児記録の詳細表示 */}
              <div className="mt-1 flex items-center gap-3">
                {careItem && <p className="max-md:text-xs">{dataMap[careItem.recordType].label}</p>}
                {careItem?.milkDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">{careItem?.milkDetail.amountMl}ml</p>
                  </>
                )}
                {careItem?.diaperDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">
                      {careItem?.diaperDetail.diaperType === "WET" ? "おしっこ" : ""}
                      {careItem?.diaperDetail.diaperType === "DIRTY" ? "うんち" : ""}
                    </p>
                  </>
                )}
                {careItem?.healthDetail && (
                  <>
                    <span className="bg-line-gray h-px w-8 max-md:w-4"></span>
                    <p className="max-md:text-xs">体温: {careItem?.healthDetail.temperature}°C</p>
                  </>
                )}
              </div>
              {/* メモ表示 */}
              <p className="mt-2 max-md:mt-1 max-md:text-xs">
                {careItem?.mealDetail?.note ||
                  careItem?.milkDetail?.note ||
                  careItem?.diaperDetail?.note ||
                  careItem?.healthDetail?.note ||
                  growthItem?.note}
              </p>
            </div>
          )}
          {/* 編集モード */}
          {isEditMode && (
            <div className="w-full">
              {/* 記録種別表示 */}
              {careItem && (
                <p className="font-medium max-md:text-xs">{dataMap[careItem.recordType].label}</p>
              )}
              {growthItem && <p className="font-medium max-md:text-xs">身長/体重</p>}
              {/* 記録詳細編集 */}
              {careItem?.recordType === "MILK" && (
                <div className="mt-3 flex items-end gap-3 max-md:gap-1">
                  <input
                    type="number"
                    min={10}
                    max={400}
                    step={10}
                    className="border-line-gray focus:outline-brown-light bg-light-dark h-8 w-20 rounded-sm border px-2 font-medium max-md:h-6 max-md:w-15 max-md:text-sm"
                    value={updateData.amountMl}
                    onChange={(e) => {
                      setUpdateData((prev) => ({ ...prev, amountMl: Number(e.target.value) }));
                    }}
                  />
                  <span className="max-md:text-xs">ml</span>
                </div>
              )}
              {careItem?.recordType === "DIAPER" && (
                <div className="mt-3 flex items-center gap-3">
                  <label htmlFor={`${uid}-wet`} className="flex items-center gap-1">
                    <input
                      type="radio"
                      className="accent-accent-pink"
                      id={`${uid}-wet`}
                      name={`${uid}-diaperType`}
                      checked={updateData.diaperType === "WET"}
                      onChange={() => {
                        setUpdateData((prev) => ({ ...prev, diaperType: "WET" }));
                      }}
                    />
                    <span className="text-sm font-medium max-md:text-xs">おしっこ</span>
                  </label>
                  <label htmlFor={`${uid}-dirty`} className="flex items-center gap-1">
                    <input
                      type="radio"
                      className="accent-accent-pink"
                      id={`${uid}-dirty`}
                      name={`${uid}-diaperType`}
                      checked={updateData.diaperType === "DIRTY"}
                      onChange={() => {
                        setUpdateData((prev) => ({ ...prev, diaperType: "DIRTY" }));
                      }}
                    />
                    <span className="text-sm font-medium max-md:text-xs">うんち</span>
                  </label>
                </div>
              )}
              {careItem?.recordType === "HEALTH" && (
                <div className="mt-3 flex items-end gap-3 max-md:gap-1">
                  <input
                    type="number"
                    min={34}
                    max={42}
                    step="0.1"
                    className="border-line-gray focus:outline-brown-light bg-light-dark h-8 w-20 border px-2 font-medium max-md:h-6 max-md:w-15 max-md:text-sm"
                    value={updateData.temperature ?? ""}
                    onChange={(e) => {
                      setUpdateData((prev) => ({
                        ...prev,
                        temperature: Number(e.target.value),
                      }));
                    }}
                  />
                  <span className="max-md:text-xs">℃</span>
                </div>
              )}
              {/* 身長/体重編集 */}
              {growthItem && (
                <div className="mt-3">
                  <div className="flex items-center gap-1">
                    <p className="text-sm font-medium max-md:text-[10px]">身長: </p>
                    <input
                      type="number"
                      min={0}
                      max={200}
                      step={0.1}
                      className="border-line-gray bg-light-dark h-8 w-20 rounded-sm border px-1.5 text-sm font-medium outline-none max-md:h-6 max-md:text-sm"
                      value={updateData.height ?? ""}
                      onChange={(e) => {
                        setUpdateData((prev) => ({
                          ...prev,
                          height: e.target.value === "" ? undefined : Number(e.target.value),
                        }));
                      }}
                      disabled={isPending}
                    />
                    <p className="text-sm font-medium max-md:text-[10px]">cm</p>
                  </div>

                  <div className="mt-2 flex items-center gap-1">
                    <p className="text-sm font-medium max-md:text-[10px]">体重: </p>
                    <input
                      type="number"
                      min={0}
                      max={200}
                      step={0.1}
                      className="border-line-gray bg-light-dark h-8 w-20 rounded-sm border px-1.5 text-sm font-medium outline-none max-md:h-6 max-md:text-sm"
                      value={updateData.weight ?? ""}
                      onChange={(e) => {
                        setUpdateData((prev) => ({
                          ...prev,
                          weight: e.target.value === "" ? undefined : Number(e.target.value),
                        }));
                      }}
                      disabled={isPending}
                    />
                    <p className="text-sm font-medium max-md:text-[10px]">kg</p>
                  </div>
                </div>
              )}
              {/* メモ編集 */}
              <input
                type="text"
                className="border-line-gray focus:outline-brown-light bg-light-dark mt-3 h-8 w-full max-w-80 rounded-sm border px-2 max-md:h-6 max-md:text-sm"
                value={updateData.note}
                onChange={(e) => setUpdateData((prev) => ({ ...prev, note: e.target.value }))}
              />

              {/* 日時編集 */}
              <div className="mt-2 flex items-center gap-5 max-md:flex-col max-md:items-start max-md:gap-2">
                {/* 身長・体重のときは時間を編集しない */}
                {!growthItem && (
                  <input
                    type="time"
                    className="font-medium outline-none max-md:text-xs"
                    value={updateData.time}
                    onChange={(e) => setUpdateData((prev) => ({ ...prev, time: e.target.value }))}
                  />
                )}

                <input
                  type="date"
                  className="font-medium outline-none max-md:text-xs"
                  value={updateData.date}
                  onChange={(e) => setUpdateData((prev) => ({ ...prev, date: e.target.value }))}
                />
              </div>
            </div>
          )}
        </div>
        <div className="mt-auto ml-auto flex w-fit shrink-0 gap-5 max-md:mt-1 max-md:gap-3">
          {/* 表示モード時のボタン */}
          {!isEditMode && (
            <>
              <button
                className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                disabled={isPending}
                onClick={() => setIsEditMode(true)}
              >
                編集
              </button>
              <button
                className="text-warning cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                onClick={() => setIsDeleteConfirmOpen(true)}
                disabled={isPending}
              >
                削除
              </button>
            </>
          )}
          {/* 編集モード時のボタン */}
          {isEditMode && (
            <>
              <button
                className="cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                disabled={isPending}
                onClick={() => setIsEditMode(false)}
              >
                戻る
              </button>
              <button
                className="text-success cursor-pointer text-sm underline transition-all hover:opacity-70 max-md:text-[10px]"
                onClick={() => setIsSaveConfirmOpen(true)}
                disabled={isPending}
              >
                更新
              </button>
            </>
          )}
        </div>
        {/* 更新確認モーダル */}
        <ConfirmModal
          isOpen={isSaveConfirmOpen}
          isPending={isPending}
          action={saveAction}
          closeAction={() => setIsSaveConfirmOpen(false)}
          message="選択した記録を更新します。"
          buttonMessage="更新する"
        />
        {/* 削除確認モーダル */}
        <ConfirmModal
          isOpen={isDeleteConfirmOpen}
          isPending={isPending}
          action={deleteAction}
          closeAction={() => setIsDeleteConfirmOpen(false)}
          message="選択した記録を削除します。"
          buttonType="remove"
          buttonMessage="削除する"
        />
      </div>
    </div>
  );
};
