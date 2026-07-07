import { Dispatch, SetStateAction } from "react";

type Props = {
  date: string;
  time: string;
  setDate: Dispatch<SetStateAction<string>>;
  setTime: Dispatch<SetStateAction<string>>;
  isPending: boolean;
};

export const SetDateAndTime = ({ date, time, setDate, setTime, isPending }: Props) => {
  return (
    <>
      <p className="mt-5 font-medium">日時</p>
      <div className="mt-1 flex items-center gap-4">
        <input
          type="time"
          className="text-2xl font-medium tracking-widest outline-0 max-md:text-2xl"
          value={time}
          onChange={(e) => setTime(e.target.value)}
          disabled={isPending}
        />
        <input
          type="date"
          className="border-line-gray bg-light-dark rounded-sm border px-4 py-2 text-xl font-medium outline-0 max-md:px-2 max-md:text-sm"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          disabled={isPending}
        />
      </div>
    </>
  );
};
