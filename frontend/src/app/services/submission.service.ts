import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Globals} from "../shared/globals";
import {Submission} from "../dtos/submission";
import {Observable} from "rxjs";
import {SubmissionDisplay} from "../dtos/submission-display";

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {
    private baseUri: string = this.globals.backendUri + '/submission';

  constructor(private httpClient: HttpClient, private globals: Globals) { }
  postSubmission(submission: Submission): Observable<any> {
      const imageFormData = new FormData();
      imageFormData.append('file', submission.image, submission.image.name);
      return this.httpClient.post<any>(this.baseUri + '/submit/' + submission.challengeId, imageFormData);
  }

    getAllSubmissions(challengeId: number): Observable<SubmissionDisplay[]> {
        return this.httpClient.get<SubmissionDisplay[]>(`${this.baseUri}/upvotes/${challengeId}`);
    }

    upvoteSubmission(id: number, isUpvote: boolean): Observable<any> {
        return this.httpClient.post<any>(`${this.baseUri}/upvote/${id}/${isUpvote}`, null);
    }

}


