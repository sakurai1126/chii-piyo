import { useRef, useState } from "react";

export const useDragAndDrop = (onFilesAdd: (files: File[]) => void) => {
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

  const handleDrop = (e: React.DragEvent<HTMLElement>) => {
    e.preventDefault();
    dragCounter.current = 0;
    setIsDragging(false);
    // 画像ファイルのみをフィルタリングしてonFilesAddを呼び出す
    const files = [...e.dataTransfer.files].filter((f) => f.type.startsWith("image/"));
    if (files.length) onFilesAdd(files);
  };

  return { isDragging, handleDrop, handleDragEnter, handleDragLeave };
};
