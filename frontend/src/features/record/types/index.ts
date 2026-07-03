export type SelectedMediaData = {
  id: number;
  url: string;
};

export type FirstRecordData = {
  id?: number;
  title: string;
  achievedDate: string;
  comment: string;
  media?: SelectedMediaData[];
};
