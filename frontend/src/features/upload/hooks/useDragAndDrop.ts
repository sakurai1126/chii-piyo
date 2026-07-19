"use client";

import { useRef, useState } from "react";

type UseDragAndDropParams = {
  onFilesAdd: (files: File[]) => void;
  acceptTypes: "image" | "video";
};

/**
 * ドラッグ＆ドロップでファイルを受け取るためのフック
 *
 * @param onFilesAdd
 * ファイルが追加されたときに呼び出される関数
 * useUploadPageのsetFileAndUrlを渡す想定
 *
 * @returns
 * - isDragging: ドラッグ中かどうかのフラグ
 * - handleDrop: ドロップした際の処理
 * - handleDragEnter: ドラッグエリアに入った際の処理
 * - handleDragLeave: ドラッグエリアから出た際の処理
 */
export const useDragAndDrop = ({ onFilesAdd, acceptTypes }: UseDragAndDropParams) => {
  // ドラッグ中かどうかを管理するフラグ
  const [isDragging, setIsDragging] = useState(false);
  // 子要素に入るたびEnterとLeaveが発火するため、ドラッグ中かどうかをカウントで管理する
  const dragCounter = useRef(0);

  // エリア内の要素にドラッグした時counterを増やしつつフラグを立てる
  const handleDragEnter = () => {
    dragCounter.current++;
    setIsDragging(true);
  };

  // エリア内の要素からドラッグが離れたときcounterを減らしつつ、counterが0(完全に外に出た状態)になったらフラグを下ろす
  const handleDragLeave = () => {
    dragCounter.current--;
    if (dragCounter.current === 0) setIsDragging(false);
  };

  // ドロップしたときはcounterをリセットしてフラグを下ろし、ファイルを処理する
  const handleDrop = (e: React.DragEvent<HTMLElement>) => {
    e.preventDefault();
    dragCounter.current = 0;
    setIsDragging(false);
    // 画像および動画ファイルのみをフィルタリングしてonFilesAddを呼び出す
    const files = [...e.dataTransfer.files].filter((f) => f.type.startsWith(`${acceptTypes}/`));
    if (files.length) onFilesAdd(files);
  };

  return { isDragging, handleDrop, handleDragEnter, handleDragLeave };
};
