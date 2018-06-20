import {GeneralLedgerRecord} from "./general.ledger.record";
export class GeneratedGLFormRecord extends GeneralLedgerRecord{

    subscriptionRedemptionEntity: string;
    nbAccountNumber: string;
    nicAccountName: string;

    added: string;
    addedRecordId: number;

    editable: boolean;
    editing: boolean
}