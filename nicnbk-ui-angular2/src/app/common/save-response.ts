import {OKResponse} from "./ok-response";

export class SaveResponse extends OKResponse{
    entityId: number;
    creationDate: string;
}