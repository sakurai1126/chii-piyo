type Props = {
  onChange: (date: string) => void;
  value?: string;
};
export const DatePicker = ({ onChange, value }: Props) => {
  // "yyyy-MM-dd"形式で今日の日付を取得
  const today = new Date().toLocaleDateString("sv-SE");
  return (
    <div>
      <p className="max-md:text-[13px]">日付</p>
      <input
        type="date"
        className="border-line-gray bg-light-dark focus:outline-brown-light mt-2 h-12 w-50 rounded-sm border px-3 max-md:h-9 max-md:w-36 max-md:text-[13px] dark:outline-none"
        onChange={(e) => onChange(e.target.value)}
        value={value ?? today}
      />
    </div>
  );
};
