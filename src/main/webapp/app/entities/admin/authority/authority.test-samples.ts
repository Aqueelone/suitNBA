import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'c62c7cee-dda7-47da-98d9-58cec4604c28',
};

export const sampleWithPartialData: IAuthority = {
  name: 'b3d605e8-c79e-4802-b4c0-3e55b6e55021',
};

export const sampleWithFullData: IAuthority = {
  name: '8d7e5325-7fc9-441d-9478-1c52e37e30f3',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
