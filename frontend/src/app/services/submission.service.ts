import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Submission} from "../dtos/submission";
import {Observable} from "rxjs";
import {SubmissionDisplay} from "../dtos/submission-display";
import {ConfigService} from "@services/config.service";

@Injectable({
  providedIn: 'root'
})
export class SubmissionService {
    private baseUri: string = this.configService.backendUri  + '/submission';

  constructor(private httpClient: HttpClient, private configService: ConfigService) { }
  postSubmission(submission: Submission): Observable<any> {
      const imageFormData = new FormData();
      imageFormData.append('file', submission.image, submission.image.name);
      return this.httpClient.post<any>(this.baseUri + '/submit/' + submission.challengeId, imageFormData);
  }

    getCurrentSubmission(challengeId: number): Observable<SubmissionDisplay> {
        return this.httpClient.get<SubmissionDisplay>(`${this.baseUri}/current/${challengeId}`);
    }
    getAllSubmissions(challengeId: number): Observable<SubmissionDisplay[]> {
        return this.httpClient.get<SubmissionDisplay[]>(`${this.baseUri}/upvotes/${challengeId}`);
    }

    upvoteSubmission(id: number, isUpvote: boolean): Observable<any> {
        return this.httpClient.post<any>(`${this.baseUri}/upvote/${id}/${isUpvote}`, null);
    }

}


