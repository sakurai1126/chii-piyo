export const GraphSummary = () => {
  return (
    <div className="mt-10 grid grid-cols-5 gap-3 max-md:mt-6 max-md:grid-cols-6 max-md:gap-1.5">
      {/* 身長 */}
      <div className="border-graph-border-height flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-3 max-md:h-30">
        <p className="max-md:text-[13px]">身長</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-4xl font-medium max-lg:text-3xl max-md:text-[28px]">65.0</p>
          <p className="text-2xl max-md:text-xl">cm</p>
        </div>
        <p className="text-note-gray mt-2 text-sm max-md:mt-1 max-md:text-xs">前月比+1.2cm</p>
      </div>
      {/* 体重 */}
      <div className="border-graph-border-weight flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-3 max-md:h-30">
        <p className="max-md:text-[13px]">体重</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-4xl font-medium max-lg:text-3xl max-md:text-[28px]">8.6</p>
          <p className="text-2xl max-md:text-xl">kg</p>
        </div>
        <p className="text-note-gray mt-2 text-sm max-md:mt-1 max-md:text-xs">前月比+0.2kg</p>
      </div>
      {/* 排泄回数 */}
      <div className="border-graph-border-diaper flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-2 max-md:h-30">
        <p className="max-md:text-xs">排泄回数</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-4xl font-medium max-lg:text-3xl max-md:text-2xl">7</p>
          <p className="text-xl max-md:text-sm">回/日</p>
        </div>
      </div>
      {/* ミルク量 */}
      <div className="border-graph-border-milk flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-2 max-md:h-30">
        <p className="max-md:text-xs">ミルク量</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-4xl font-medium max-lg:text-3xl max-md:text-2xl">620</p>
          <p className="text-xl max-md:text-sm">ml/日</p>
        </div>
      </div>
      {/* 覚えた言葉の数 */}
      <div className="border-graph-border-word flex h-40 flex-col items-center justify-center rounded-lg border bg-white/50 text-center backdrop-blur-[7.5px] max-md:col-span-2 max-md:h-30">
        <p className="max-md:text-xs">覚えた言葉の数</p>
        <div className="mt-2 flex items-end gap-1 max-md:mt-1">
          <p className="text-4xl font-medium max-lg:text-3xl max-md:text-2xl">13</p>
          <p className="text-xl max-md:text-sm">語</p>
        </div>
      </div>
    </div>
  );
};
