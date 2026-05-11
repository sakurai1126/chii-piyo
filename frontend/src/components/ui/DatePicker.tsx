type Props = {
  onChange: (date: string) => void;
};
export const DatePicker = ({ onChange }: Props) => {
  // "yyyy-MM-dd"形式で今日の日付を取得
  const today = new Date().toLocaleDateString("sv-SE");
  return (
    <div>
      <p className="max-md:text-[13px]">日付</p>
      <input
        type="date"
        defaultValue={today}
        className="focus:outline-brown-light border-line-gray mt-2 h-12 w-50 rounded-sm border bg-white px-3 max-md:h-9 max-md:w-36 max-md:text-[13px]"
        onChange={(e) => onChange(e.target.value)}
      />
    </div>
  );
};
